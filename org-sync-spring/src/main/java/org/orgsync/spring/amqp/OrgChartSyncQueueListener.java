package org.orgsync.spring.amqp;

import static org.orgsync.core.Constants.ERROR_PREFIX;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    public OrgChartSyncQueueListener(SyncEngine syncEngine, ObjectMapper objectMapper) {
        this.syncEngine = syncEngine;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("${orgsync.amqp.org-chart.sync.queue:orgsync.org-chart.sync.queue}"),
            exchange = @Exchange("${orgsync.amqp.org-chart.sync.exchange:dop_user_company_sync}"),
            key = "${orgsync.amqp.org-chart.sync.routing-key:user_company.sync}"

    ))
    public void handleOrgChartSyncRequest(Object payload) {
        OrgChartSyncPayload orgChartSyncPayload = resolvePayload(payload);
        if (orgChartSyncPayload == null || !StringUtils.hasText(orgChartSyncPayload.companyUuid())) {
            throw new IllegalArgumentException(ERROR_PREFIX + "companyUuid is required in org chart sync event");
        }

        if (orgChartSyncPayload.logSeq() == null) {
            throw new IllegalArgumentException(ERROR_PREFIX + "logSeq is required in org chart sync event");
        }

        syncEngine.synchronizeCompany(orgChartSyncPayload.companyUuid(), orgChartSyncPayload.logSeq());
    }

    private OrgChartSyncPayload resolvePayload(Object payload) {
        if (payload == null) {
            return null;
        }

        if (payload instanceof OrgChartSyncPayload orgChartSyncPayload) {
            return orgChartSyncPayload;
        }

        if (payload instanceof OrgChartSyncMessage orgChartSyncMessage) {
            return orgChartSyncMessage.messagePayload();
        }

        if (payload instanceof String rawPayload) {
            return readPayload(rawPayload);
        }

        return convertPayload(payload);
    }

    private OrgChartSyncPayload readPayload(String rawPayload) {
        if (!StringUtils.hasText(rawPayload)) {
            return null;
        }
        try {
            OrgChartSyncMessage message = objectMapper.readValue(rawPayload, OrgChartSyncMessage.class);
            if (message != null && message.messagePayload() != null) {
                return message.messagePayload();
            }
        }
        catch (Exception ignored) {
            // fall through and attempt payload parsing
        }

        try {
            return objectMapper.readValue(rawPayload, OrgChartSyncPayload.class);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException(ERROR_PREFIX + "Unable to parse org chart sync payload", ex);
        }
    }

    private OrgChartSyncPayload convertPayload(Object payload) {
        try {
            OrgChartSyncMessage message = objectMapper.convertValue(payload, OrgChartSyncMessage.class);
            if (message != null && message.messagePayload() != null) {
                return message.messagePayload();
            }
        }
        catch (IllegalArgumentException ignored) {
            // fall through
        }

        try {
            return objectMapper.convertValue(payload, OrgChartSyncPayload.class);
        }
        catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(ERROR_PREFIX + "Unable to convert org chart sync payload", ex);
        }
    }
}
