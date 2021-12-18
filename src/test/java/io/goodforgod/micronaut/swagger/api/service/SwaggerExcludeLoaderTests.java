package io.goodforgod.micronaut.swagger.api.service;


import io.goodforgod.micronaut.swagger.api.config.SwaggerConfig;
import io.goodforgod.micronaut.swagger.api.model.Resource;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * @author Anton Kurako (GoodforGod)
 * @since 25.9.2020
 */
@MicronautTest(propertySources = "classpath:application-exclude.yml")
class SwaggerExcludeLoaderTests extends Assertions {

    @Inject
    private SwaggerLoader loader;
    @Inject
    private SwaggerConfig config;

    @Test
    void noSwaggersPresent() {
        final String fileName = "swagger.yml";
        assertTrue(config.getExclude().contains(fileName));

        final Resource resource = loader.getMergedSwagger().block();
        assertNull(resource);
    }
}
