package io.goodforgod.micronaut.openapi.service;

import io.goodforgod.micronaut.openapi.OpenAPISettings;
import io.goodforgod.micronaut.openapi.config.OpenAPIConfig;
import io.goodforgod.micronaut.openapi.config.RapidocConfig;
import io.goodforgod.micronaut.openapi.config.SwaggerUIConfig;
import io.goodforgod.micronaut.openapi.model.Resource;
import io.goodforgod.micronaut.openapi.model.URIResource;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 25.9.2020
 */
@MicronautTest
class OpenAPIProviderTests extends Assertions {

    @Inject
    private OpenAPIProvider loader;
    @Inject
    private OpenAPIConfig config;
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

        assertEquals(OpenAPISettings.DEFAULT_OPENAPI_URL, config.getPath());

        assertEquals(OpenAPISettings.DEFAULT_RAPIDOC_URL, rapidocConfig.getPath());
        assertTrue(rapidocConfig.isEnabled());

        assertEquals(OpenAPISettings.DEFAULT_SWAGGER_UI_URL, swaggerUIConfig.getPath());
        assertTrue(swaggerUIConfig.isEnabled());
    }

    @Test
    void isServicePresent() {
        final Resource resource = loader.getAny().get();
        assertNotNull(resource);
        assertNotNull(resource.toString());
        assertNotEquals(0, resource.hashCode());
    }

    @Test
    void isNonMergedPresent() {
        final Resource resource = loader.getMerged().get();
        assertNotNull(resource);
        assertNotNull(resource.toString());
        assertNotEquals(0, resource.hashCode());
    }

    @Test
    void isServiceYamlPresentAsMerged() {
        final Resource resource = loader.getMerged().get();
        assertNotNull(resource);

        final Resource resourceCached = loader.getMerged().get();
        assertNotNull(resourceCached);
        assertEquals(resource, resourceCached);
    }

    @Test
    void getMergedAndCachedMerged() throws URISyntaxException {
        final List<Resource> resources = new ArrayList<>();
        resources.add(URIResource.of(new URI("mock/test-1.yml")));
        resources.add(URIResource.of(new URI("META-INF/swagger/swagger.yml")));

        final OpenAPIProvider openAPIProvider = new OpenAPIProvider(config, merger) {

            @Override
            public Collection<Resource> getAll() {
                return resources;
            }
        };

        final Resource resource = openAPIProvider.getMerged().get();
        assertNotNull(resource);

        final Resource resourceCached = openAPIProvider.getMerged().get();
        assertNotNull(resourceCached);
        assertEquals(resource, resourceCached);
    }

    @Test
    void noSwaggerPresent() {
        final OpenAPIProvider openAPIProvider = new OpenAPIProvider(config, merger) {

            @Override
            public Collection<Resource> getAll() {
                return Collections.emptyList();
            }
        };

        final Optional<Resource> resource = openAPIProvider.getMerged();
        assertFalse(resource.isPresent());
    }

    @Test
    void isSingleOnlyPresent() {
        final Collection<Resource> resources = loader.getAll();
        assertNotNull(resources);
        assertFalse(resources.isEmpty());
    }
}
