package io.goodforgod.micronaut.openapi.service;

import io.goodforgod.micronaut.openapi.config.OpenAPIConfig;
import io.goodforgod.micronaut.openapi.model.Resource;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 25.9.2020
 */
@MicronautTest(propertySources = "classpath:application-exclude-filepath.yml")
class OpenAPIExcludePathTests extends Assertions {

    @Inject
    private OpenAPIProvider loader;
    @Inject
    private OpenAPIConfig config;

    @Test
    void FilePathsNotPresent() {
        final String fileName = "META-INF/swagger/swagger.yml";
        assertTrue(config.getExclude().contains(fileName));

        final Optional<Resource> resource = loader.getMerged();
        assertTrue(resource.isEmpty());
    }
}
