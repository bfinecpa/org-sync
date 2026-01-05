package org.orgsync.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

/**
 * Loads org-sync configuration from YAML resources.
 */
public final class OrgSyncYamlLoader {

    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

    private OrgSyncYamlLoader() {
    }

    public static OrgSyncProperties loadFromClasspath(String resourceName) {
        Objects.requireNonNull(resourceName, "resourceName");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(resourceName)) {
            if (inputStream == null) {
                throw new IllegalStateException("Could not find resource: " + resourceName);
            }
            return YAML_MAPPER.readValue(inputStream, OrgSyncProperties.class);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load org-sync configuration from " + resourceName, e);
        }
    }
}
