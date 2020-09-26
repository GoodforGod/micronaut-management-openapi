package micronaut.swagger.api.service;

import io.micronaut.test.annotation.MicronautTest;
import micronaut.swagger.api.SwaggerSettings;
import micronaut.swagger.api.config.SwaggerConfig;
import micronaut.swagger.api.model.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 25.9.2020
 */
@MicronautTest
class SwaggerLoaderTests extends Assertions {

    private static final Resource EMPTY = new Resource(null, 0);

    @Inject
    private SwaggerLoader loader;

    @Inject
    private SwaggerConfig config;

    @Inject
    private YamlMerger merger;

    @Test
    void isConfigValid() {
        assertNotNull(config.getExclude());
        assertFalse(config.getExclude().isEmpty());
        assertEquals(2, config.getExclude().size());

        assertEquals(SwaggerSettings.DEFAULT_SWAGGER_URL, config.getPath());

        assertNotNull(config.getRapidocConfig());
        assertEquals(SwaggerSettings.DEFAULT_RAPIDOC_URL, config.getRapidocConfig().getPath());
        assertFalse(config.getRapidocConfig().isEnabled());

        assertNotNull(config.getUiConfig());
        assertEquals(SwaggerSettings.DEFAULT_SWAGGER_UI_URL, config.getUiConfig().getPath());
        assertTrue(config.getUiConfig().isEnabled());
    }

    @Test
    void isServicePresent() {
        final Resource resource = loader.getServiceSwagger().blockingGet(EMPTY);
        assertNotNull(resource);
        assertNotEquals(EMPTY, resource);
        assertNotNull(resource.toString());
        assertNotEquals(0, resource.hashCode());
        assertNotEquals(0, resource.getCreated());
    }

    @Test
    void isNonMergedPresent() {
        config.setMerge(false);

        final Resource resource = loader.getMergedSwagger().blockingGet(EMPTY);
        assertNotNull(resource);
        assertNotEquals(EMPTY, resource);
        assertNotNull(resource.toString());
        assertNotEquals(0, resource.hashCode());
        assertNotEquals(0, resource.getCreated());

        config.setMerge(true);
    }

    @Test
    void isServiceYamlPresentAsMerged() {
        final Resource resource = loader.getMergedSwagger().blockingGet(EMPTY);
        assertNotNull(resource);
        assertNotEquals(EMPTY, resource);

        final Resource resourceCached = loader.getMergedSwagger().blockingGet(EMPTY);
        assertNotNull(resourceCached);
        assertNotEquals(EMPTY, resourceCached);
        assertEquals(resource, resourceCached);
    }

    @Test
    void getMergedAndCachedMerged() throws URISyntaxException {
        final List<Resource> resources = List.of(
                new Resource(new URI("test-1.yml"), 20),
                new Resource(new URI("META-INF/swagger/swagger.yml"), 10));

        final SwaggerLoader swaggerLoader = new SwaggerLoader(config, merger) {

            @Override
            public Collection<Resource> getSwaggers() {
                return resources;
            }
        };

        final Resource resource = swaggerLoader.getMergedSwagger().blockingGet(EMPTY);
        assertNotNull(resource);
        assertNotEquals(EMPTY, resource);

        final Resource resourceCached = swaggerLoader.getMergedSwagger().blockingGet(EMPTY);
        assertNotNull(resourceCached);
        assertNotEquals(EMPTY, resourceCached);
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

        final Resource resource = swaggerLoader.getMergedSwagger().blockingGet(EMPTY);
        assertNotNull(resource);
        assertEquals(EMPTY, resource);
    }

    @Test
    void noSwaggersPresent() {
        final List<String> prev = config.getExclude();
        config.setExclude(List.of("swagger.yml"));

        final Resource resource = loader.getMergedSwagger().blockingGet(EMPTY);
        assertNotNull(resource);
        assertNotEquals(EMPTY, resource);

        config.setExclude(prev);
    }

    @Test
    void isSingleOnlyPresent() {
        final Collection<Resource> resources = loader.getSwaggers();
        assertNotNull(resources);
        assertFalse(resources.isEmpty());
    }
}
