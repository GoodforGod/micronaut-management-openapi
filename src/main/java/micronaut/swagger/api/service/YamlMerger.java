package micronaut.swagger.api.service;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.CollectionUtils;
import micronaut.swagger.api.config.SwaggerConfig;
import micronaut.swagger.api.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import javax.inject.Singleton;
import java.io.InputStream;
import java.util.*;

/**
 * Yaml file merged
 *
 * @author Anton Kurako (GoodforGod)
 * @since 21.9.2020
 */
@Requires(beans = SwaggerConfig.class)
@Singleton
public class YamlMerger {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Map<Object, Object> merge(Collection<Resource> resources) {
        if (CollectionUtils.isEmpty(resources))
            return Collections.emptyMap();

        final Map<Object, Object> result = new LinkedHashMap<>();

        resources.stream()
                .map(this::swaggerAsMap)
                .forEach(yaml -> merge(result, yaml));

        return result;
    }

    private Map<Object, Object> swaggerAsMap(Resource resource) {
        final String path = resource.getUri().getPath();
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream == null) {
            logger.debug("Reading as stream swagger at path: {}", path);
            final int metaIndex = path.lastIndexOf("META-INF");
            final int index = metaIndex == -1 ? 0 : metaIndex;
            final String localPath = path.substring(index);
            stream = getClass().getResourceAsStream("/" + localPath);
        }

        if (stream == null)
            throw new IllegalArgumentException("Swagger can not be loaded as resource from path:" + resource.getUri());

        return new Yaml().load(stream);
    }

    @SuppressWarnings("unchecked")
    private void merge(Map<Object, Object> merged, Map<Object, Object> yaml) {
        if (CollectionUtils.isEmpty(yaml))
            return;

        for (Map.Entry<Object, Object> entry : yaml.entrySet()) {
            final Object v = entry.getValue();
            final Object key = entry.getKey();
            if (v == null) {
                merged.put(key, null);
                continue;
            }

            final Object existingValue = merged.get(key);
            if (existingValue != null) {
                if (v instanceof Map) {
                    if (existingValue instanceof Map) {
                        merge((Map<Object, Object>) existingValue, (Map<Object, Object>) v);
                    } else if (existingValue instanceof String
                            || existingValue instanceof Boolean
                            || existingValue instanceof Double
                            || existingValue instanceof Integer) {
                        throw new IllegalArgumentException(
                                "Cannot merge complex element into a simple element: " + key);
                    } else {
                        throwUnknownValueType(key, v);
                    }
                } else if (v instanceof List) {
                    final Object value = merged.get(key);
                    final boolean v2 = value instanceof List;
                    if (!v2)
                        throw new IllegalArgumentException("Cannot merge a list with a non-list: " + key);

                    ((List<Object>) value).addAll(((List<Object>) v));
                } else if (v instanceof String
                        || v instanceof Boolean
                        || v instanceof Double
                        || v instanceof Integer) {
                    merged.put(key, v);
                } else {
                    throwUnknownValueType(key, v);
                }
            } else {
                if (v instanceof Map
                        || v instanceof List
                        || v instanceof String
                        || v instanceof Boolean
                        || v instanceof Integer
                        || v instanceof Double) {
                    merged.put(key, v);
                } else {
                    throwUnknownValueType(key, v);
                }
            }
        }
    }

    private void throwUnknownValueType(Object key, Object yamlValue) {
        throw new IllegalArgumentException("Cannot merge element of unknown type: " + key + ": " + yamlValue.getClass().getSimpleName());
    }
}
