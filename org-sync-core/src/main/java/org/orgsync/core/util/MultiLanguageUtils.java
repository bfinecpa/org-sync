package org.orgsync.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.orgsync.core.Constants;
import org.orgsync.core.dto.MultiLanguageDto;
import org.orgsync.core.dto.MultiLanguageType;
import org.orgsync.core.dto.TargetDomain;

public class MultiLanguageUtils {

    public static Map<MultiLanguageType, MultiLanguageDto> parseJson(Long id, TargetDomain targetDomain,  String json) {
        ObjectMapper mapper = new ObjectMapper();
        Map<MultiLanguageType, MultiLanguageDto> map = new HashMap<>();
        try {
            Map<String, String> multiLangauges = mapper.readValue(json, new TypeReference<>() {});
            for (Entry<String, String> entry : multiLangauges.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (value == null || value.isEmpty()) {
                    continue;
                }

                MultiLanguageType multiLanguageType = MultiLanguageType.valueOf(key);
                MultiLanguageDto multiLanguageDto = new MultiLanguageDto(id, targetDomain, multiLanguageType, value);

                map.put(multiLanguageType, multiLanguageDto);
            }
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(Constants.ERROR_PREFIX+"error parsing json in MultiLanguageUtils", e);
        }

        return map;
    }

}
