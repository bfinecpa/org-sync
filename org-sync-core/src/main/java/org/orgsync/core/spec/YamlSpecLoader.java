package org.orgsync.core.spec;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Loads YAML projection specifications from disk.
 */
public class YamlSpecLoader {

    public YamlSyncSpec load(Path path) {
        try (InputStream inputStream = Files.newInputStream(path)) {
            // TODO: wire a YAML parser such as SnakeYAML to populate the spec
            return new YamlSyncSpec(Map.of());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load sync spec from " + path, e);
        }
    }
}
