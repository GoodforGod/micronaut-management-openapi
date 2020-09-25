package micronaut.swagger.api.service;

import io.micronaut.test.annotation.MicronautTest;
import micronaut.swagger.api.model.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Description in progress
 *
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
                new Resource(new URI("test-1.yml"), 20),
                new Resource(new URI("META-INF/swagger/swagger.yml"), 10));

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
        final List<Resource> resources = List.of(new Resource(new URI("test-1.yml"), 20));

        final Map<Object, Object> merged = merger.merge(resources);
        assertNotNull(merged);
        assertFalse(merged.isEmpty());
    }
}
