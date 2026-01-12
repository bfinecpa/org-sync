package org.orgsync.spring.amqp;

import static org.orgsync.core.Constants.ORG_SYNC_PREFIX;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.orgsync.core.engine.SyncEngine;
import org.springframework.amqp.core.Message;
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
        value = @Queue(value = "orgsync.org-chart.sync.queue", durable = "true"),
        exchange = @Exchange(value = "dop_user_company_sync.fanout", type = "fanout", durable = "true")
    ))
    public void handleOrgChartSyncRequest(Object payload) {
        System.out.println("OrgChartSyncQueueListener received org-sync request: " + payload);
        OrgChartSyncPayload orgChartSyncPayload = resolvePayload(payload);
        if (orgChartSyncPayload == null || !StringUtils.hasText(orgChartSyncPayload.companyUuid())) {
            throw new IllegalArgumentException(ORG_SYNC_PREFIX + "companyUuid is required in org chart sync event");
        }

        if (orgChartSyncPayload.logSeq() == null) {
            throw new IllegalArgumentException(ORG_SYNC_PREFIX + "logSeq is required in org chart sync event");
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

        if (payload instanceof Message amqpMessage) {
            byte[] body = amqpMessage.getBody();
            if (body == null || body.length == 0) {
                return null;
            }
            return readPayload(new String(body, StandardCharsets.UTF_8));
        }

        if (payload instanceof byte[] rawBytes) {
            if (rawBytes.length == 0) {
                return null;
            }
            return readPayload(new String(rawBytes, StandardCharsets.UTF_8));
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
            throw new IllegalArgumentException(ORG_SYNC_PREFIX + "Unable to parse org chart sync payload", ex);
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
            throw new IllegalArgumentException(ORG_SYNC_PREFIX + "Unable to convert org chart sync payload", ex);
        }
    }
}
