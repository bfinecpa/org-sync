package org.orgsync.core.engine;

import org.orgsync.core.Constants;
import org.orgsync.core.client.OrgChartClient;
import org.orgsync.core.dto.ProvisionSequenceDto;
import org.orgsync.core.lock.LockManager;
import org.orgsync.core.logging.SyncLogger;
import org.orgsync.core.service.OrgSyncCompanyGroupService;
import org.orgsync.core.service.OrgSyncCompanyService;
import org.orgsync.core.service.OrgSyncDepartmentService;
import org.orgsync.core.service.OrgSyncIntegrationService;
import org.orgsync.core.service.OrgSyncLogSeqService;
import org.orgsync.core.service.OrgSyncMemberService;
import org.orgsync.core.service.OrgSyncMultiLanguageService;
import org.orgsync.core.service.OrgSyncOrganizationCodeService;
import org.orgsync.core.service.OrgSyncUserGroupCodeUserService;
import org.orgsync.core.service.OrgSyncUserService;
import org.orgsync.core.transaction.TransactionRunner;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Coordinates synchronization by pulling data from the org chart server and applying it
 * to downstream repositories.
 */
public class SyncEngine {

    private final OrgChartClient client;
    private final OrgSyncLogSeqService logSeqService;
    private final LockManager lockManager;
    private final TransactionRunner transactionRunner;
    private final SyncLogger logger;
    private final SyncLogApplier logApplier;
    private final SyncSnapshotApplier snapshotApplier;

    public SyncEngine(OrgChartClient client, OrgSyncLogSeqService logSeqService, LockManager lockManager,
        TransactionRunner transactionRunner, OrgSyncOrganizationCodeService organizationCodeService,
        OrgSyncDepartmentService departmentService, OrgSyncUserService userService, OrgSyncMemberService memberService,
        OrgSyncIntegrationService integrationService, OrgSyncCompanyGroupService companyGroupService,
        OrgSyncCompanyService companyService, OrgSyncUserGroupCodeUserService userGroupCodeUserService,
        OrgSyncMultiLanguageService multiLanguageService, ObjectMapper objectMapper, SyncLogger logger) {
        this.client = client;
        this.logSeqService = logSeqService;
        this.lockManager = lockManager;
        this.transactionRunner = transactionRunner;
        this.logger = logger == null ? SyncLogger.noop() : logger;
        this.logApplier = new SyncLogApplier(logSeqService, organizationCodeService, departmentService, userService,
            memberService, integrationService, companyGroupService, companyService, userGroupCodeUserService,
            multiLanguageService, objectMapper, this.logger);
        this.snapshotApplier = new SyncSnapshotApplier(client, logSeqService, organizationCodeService,
            departmentService, userService, memberService, integrationService, companyGroupService, companyService,
            userGroupCodeUserService, multiLanguageService, this.logger);
    }

    public void synchronizeCompany(String companyUuid, Long logSeq) {
        info(companyUuid, "Start lock. logSeq :" + logSeq);
        lockManager.withLock(companyUuid, () -> doSynchronize(companyUuid, logSeq));
        info(companyUuid, "End lock . logSeq :" + logSeq);

    }

    private void doSynchronize(String companyUuid, long logSeq) {
        info(companyUuid, "Start transaction. logSeq :" + logSeq);
        transactionRunner.run(() -> doSynchronizeInternal(companyUuid, logSeq));
        info(companyUuid, "End transaction. logSeq :" + logSeq);
    }

    private void doSynchronizeInternal(String companyUuid, long newLogSeq) {
        info(companyUuid, "Start sync. logSeq :" + newLogSeq);
        long currentLogSeq = logSeqService.getLogSeq(companyUuid).orElse(-1L);
        if (newLogSeq <= currentLogSeq) {
            info(companyUuid, "Skip sync. , newLogSeq=" + newLogSeq + ", currentLogSeq=" + currentLogSeq);
            return;
        }

        ProvisionSequenceDto response = client.fetchChanges(companyUuid, currentLogSeq);

        if (response.needSnapshot()) {
            long lastLogSeq = snapshotApplier.applySnapshot(companyUuid, response);
            ProvisionSequenceDto postSnapshotResponse = client.fetchChanges(companyUuid, lastLogSeq);
            applyLogInfos(companyUuid, lastLogSeq, postSnapshotResponse);
            return;
        }

        applyLogInfos(companyUuid, currentLogSeq, response);
    }

    private void applyLogInfos(String companyUuid, long currentLogSeq, ProvisionSequenceDto response) {
        info(companyUuid, "Apply delta. logSeq :" + currentLogSeq);
        long lastCursor = currentLogSeq;

        while (true) {
            logApplier.applyLogInfos(companyUuid, response.logInfoList(), response.logSeq());

            if (!response.needUpdateNextLog()) {
                info(companyUuid, "Delta applied. lastLogSeq: " + lastCursor);
                return;
            }

            long nextCursor = response.logSeq();

            // 진행이 없으면(같거나 감소) 무한루프/서버버그/데이터꼬임 가능성 → 즉시 실패로 드러내기
            if (nextCursor <= lastCursor) {
                error(companyUuid, "Non-increasing logSeq. lastCursor=" + lastCursor + ", nextCursor=" + nextCursor, null);
                throw new IllegalStateException(Constants.ORG_SYNC_PREFIX + "Non-increasing logSeq.");
            }

            lastCursor = nextCursor;
            response = client.fetchChanges(companyUuid, nextCursor);
        }
    }

    private void info(String companyUuid, String message) {
        logger.info(Constants.ORG_SYNC_LOG_PREFIX + companyUuid + ", message: " + message);
    }

    private void error(String companyUuid, String message, Exception e) {
        logger.error(Constants.ORG_SYNC_LOG_PREFIX + companyUuid + ", message: " + message, e);
    }
}
