package org.orgsync.spring.amqp;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class MessagePayloadJsonStringDeserializer extends JsonDeserializer<OrgChartSyncPayload> {

    @Override
    public OrgChartSyncPayload deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        // messagePayload 필드는 JSON 문자열 형태로 들어옴
        String raw = p.getValueAsString();
        if (raw == null || raw.isBlank()) {
            return null;
        }

        // JsonParser codec는 보통 ObjectMapper
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        return mapper.readValue(raw, OrgChartSyncPayload.class);
    }
}
