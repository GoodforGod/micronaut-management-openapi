package io.goodforgod.micronaut.swagger.api.service;


import io.goodforgod.micronaut.swagger.api.SwaggerSettings;
import io.goodforgod.micronaut.swagger.api.config.RapidocConfig;
import io.goodforgod.micronaut.swagger.api.config.SwaggerConfig;
import io.goodforgod.micronaut.swagger.api.config.SwaggerUIConfig;
import io.goodforgod.micronaut.swagger.api.model.Resource;
import io.goodforgod.micronaut.swagger.api.model.URIResource;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * @author Anton Kurako (GoodforGod)
 * @since 25.9.2020
 */
@MicronautTest
class SwaggerLoaderTests extends Assertions {

    @Inject
    private SwaggerLoader loader;
    @Inject
    private SwaggerConfig config;
    @Inject
    private RapidocConfig rapidocConfig;
    @Inject
    private SwaggerUIConfig swaggerUIConfig;
    @Inject
    private YamlMerger merger;

    @Test
    void isConfigValid() {
        assertNotNull(config.getExclude());
        assertFalse(config.getExclude().isEmpty());
        assertEquals(2, config.getExclude().size());

        assertEquals(SwaggerSettings.DEFAULT_SWAGGER_URL, config.getPath());

        assertEquals(SwaggerSettings.DEFAULT_RAPIDOC_URL, rapidocConfig.getPath());
        assertFalse(rapidocConfig.isEnabled());

        assertEquals(SwaggerSettings.DEFAULT_SWAGGER_UI_URL, swaggerUIConfig.getPath());
        assertTrue(swaggerUIConfig.isEnabled());
    }

    @Test
    void isServicePresent() {
        final Resource resource = loader.getFirstSwagger().block();
        assertNotNull(resource);
        assertNotNull(resource.toString());
        assertNotEquals(0, resource.hashCode());
    }

    @Test
    void isNonMergedPresent() {
        final Resource resource = loader.getMergedSwagger().block();
        assertNotNull(resource);
        assertNotNull(resource.toString());
        assertNotEquals(0, resource.hashCode());
    }

    @Test
    void isServiceYamlPresentAsMerged() {
        final Resource resource = loader.getMergedSwagger().block();
        assertNotNull(resource);

        final Resource resourceCached = loader.getMergedSwagger().block();
        assertNotNull(resourceCached);
        assertEquals(resource, resourceCached);
    }

    @Test
    void getMergedAndCachedMerged() throws URISyntaxException {
        final List<Resource> resources = List.of(
                URIResource.of(new URI("mock/test-1.yml")),
                URIResource.of(new URI("META-INF/swagger/swagger.yml")));

        final SwaggerLoader swaggerLoader = new SwaggerLoader(config, merger) {

            @Override
            public Collection<Resource> getSwaggers() {
                return resources;
            }
        };

        final Resource resource = swaggerLoader.getMergedSwagger().block();
        assertNotNull(resource);

        final Resource resourceCached = swaggerLoader.getMergedSwagger().block();
        assertNotNull(resourceCached);
        assertEquals(resource, resourceCached);
    }

    @Test
    void noSwaggerPresent() {
        final SwaggerLoader swaggerLoader = new SwaggerLoader(config, merger) {

            @Override
            public Collection<Resource> getSwaggers() {
                return Collections.emptyList();
            }
        };

        final Resource resource = swaggerLoader.getMergedSwagger().block();
        assertNull(resource);
    }

    @Test
    void isSingleOnlyPresent() {
        final Collection<Resource> resources = loader.getSwaggers();
        assertNotNull(resources);
        assertFalse(resources.isEmpty());
    }
}
