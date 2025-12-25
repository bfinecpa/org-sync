package org.orgsync.spring.amqp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrgChartSyncMessage(
    @JsonDeserialize(using = MessagePayloadJsonStringDeserializer.class)
    OrgChartSyncPayload messagePayload
) {
}
