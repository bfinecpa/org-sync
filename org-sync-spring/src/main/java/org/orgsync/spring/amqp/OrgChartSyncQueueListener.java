package org.orgsync.spring.amqp;

import static org.orgsync.core.Constants.ERROR_PREFIX;

import org.orgsync.core.engine.SyncEngine;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.util.StringUtils;

/**
 * Listens for organization chart synchronization requests on RabbitMQ and triggers synchronization.
 */
public class OrgChartSyncQueueListener {

    private final SyncEngine syncEngine;

    public OrgChartSyncQueueListener(SyncEngine syncEngine) {
        this.syncEngine = syncEngine;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("${orgsync.amqp.org-chart.sync.queue:orgsync.org-chart.sync.queue}"),
            exchange = @Exchange("${orgsync.amqp.org-chart.sync.exchange:dop_user_company_sync}"),
            key = "${orgsync.amqp.org-chart.sync.routing-key:user_company.sync}"

    ))
    public void handleOrgChartSyncRequest(OrgChartSyncPayload orgChartSyncPayload) {
        if (orgChartSyncPayload == null || !StringUtils.hasText(orgChartSyncPayload.companyUuid())) {
            throw new IllegalArgumentException(ERROR_PREFIX + "companyId is required in org chart sync event");
        }

        if (orgChartSyncPayload.logSeq() == null) {
            throw new IllegalArgumentException(ERROR_PREFIX + "logSeq is required in org chart sync event");
        }

        syncEngine.synchronizeCompany(orgChartSyncPayload.companyUuid(), orgChartSyncPayload.logSeq());
    }
}
