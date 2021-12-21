package io.goodforgod.micronaut.openapi.service;

import io.goodforgod.micronaut.openapi.model.Resource;
import io.goodforgod.micronaut.openapi.model.URIResource;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 25.9.2020
 */
@MicronautTest
class YamlMergedTests extends Assertions {

    @Inject
    private YamlMerger merger;

    @Test
    void bothMerged() throws URISyntaxException {
        final List<Resource> resources = List.of(
                URIResource.of(new URI("mock/test-1.yml")),
                URIResource.of(new URI("META-INF/swagger/swagger.yml")));

        final Map<Object, Object> merged = merger.merge(resources);
        assertNotNull(merged);
        assertFalse(merged.isEmpty());
    }

    @Test
    void noMergeRequired() {
        final Map<Object, Object> merged = merger.merge(Collections.emptyList());
        assertNotNull(merged);
        assertTrue(merged.isEmpty());
    }

    @Test
    void singleIsNotMerged() throws URISyntaxException {
        final List<Resource> resources = List.of(URIResource.of(new URI("mock/test-1.yml")));

        final Map<Object, Object> merged = merger.merge(resources);
        assertNotNull(merged);
        assertFalse(merged.isEmpty());
    }
}
