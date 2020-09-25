package micronaut.swagger.api.service;

import io.micronaut.test.annotation.MicronautTest;
import micronaut.swagger.api.SwaggerSettings;
import micronaut.swagger.api.config.SwaggerConfig;
import micronaut.swagger.api.model.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
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

    @Test
    void isConfigValid() {
        assertNotNull(config.getExclude());
        assertTrue(config.getExclude().isEmpty());
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
    void isMergedPresent() {
        final Resource resource = loader.getMergedSwagger().blockingGet(EMPTY);
        assertNotNull(resource);
        assertNotEquals(EMPTY, resource);

        final Resource resourceCached = loader.getMergedSwagger().blockingGet(EMPTY);
        assertNotNull(resourceCached);
        assertNotEquals(EMPTY, resourceCached);
        assertEquals(resource, resourceCached);
    }

    @Test
    void noSwaggersPresent() {
        config.setExclude(List.of("swagger.yml"));

        final Resource resource = loader.getMergedSwagger().blockingGet(EMPTY);
        assertNotNull(resource);
        assertNotEquals(EMPTY, resource);

        config.setExclude(null);
    }

    @Test
    void isSingleOnlyPresent() {
        final List<Resource> resources = loader.getSwaggers().toList().blockingGet();
        assertNotNull(resources);
        assertFalse(resources.isEmpty());
    }
}
