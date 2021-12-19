package io.goodforgod.micronaut.openapi.service;


import io.goodforgod.micronaut.openapi.model.BufferedResource;
import io.goodforgod.micronaut.openapi.model.Resource;
import io.goodforgod.micronaut.openapi.model.URLResource;
import io.micronaut.core.util.CollectionUtils;
import jakarta.inject.Singleton;
import java.io.InputStream;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;


/**
 * Service for merging YAML resources
 *
 * @author Anton Kurako (GoodforGod)
 * @since 21.9.2020
 */
@Singleton
public class YamlMerger {

    /**
     * @param resources YAML resources to merge
     * @return merged yaml in one map
     */
    public Map<Object, Object> merge(Collection<Resource> resources) {
        if (CollectionUtils.isEmpty(resources))
            return Collections.emptyMap();

        final Map<Object, Object> result = new LinkedHashMap<>();

        resources.stream()
                .map(this::getYamlAsMap)
                .forEach(yaml -> mergeYamlFiles(result, yaml));

        return result;
    }

    /**
     * @param resource YAML file to convert as map
     * @return YAML file as map
     */
    private Map<Object, Object> getYamlAsMap(@NotNull Resource resource) {
        if (resource instanceof BufferedResource) {
            return new Yaml().load(((BufferedResource) resource).getValue());
        } else {
            final InputStream inputStream = resource.getStream();
            if (inputStream == null) {
                if (resource instanceof URLResource) {
                    throw new IllegalArgumentException(
                            "Swagger can't be loaded as resource from path:" + ((URLResource) resource).getURL());
                } else {
                    throw new IllegalArgumentException("Swagger can't be loaded as resource");
                }
            }

            return new Yaml().load(inputStream);
        }
    }

    /**
     * @param merged core YAML file all others are merged into
     * @param yaml   to merge into merged file
     */
    @SuppressWarnings("unchecked")
    private void mergeYamlFiles(Map<Object, Object> merged, Map<Object, Object> yaml) {
        if (CollectionUtils.isEmpty(yaml)) {
            return;
        }

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
                        mergeYamlFiles((Map<Object, Object>) existingValue, (Map<Object, Object>) v);
                    } else {
                        throw new IllegalArgumentException("Can't merge simple type to map: " + existingValue);
                    }
                } else if (v instanceof List) {
                    final Object value = merged.get(key);
                    final boolean v2 = value instanceof List;
                    if (!v2)
                        throw new IllegalArgumentException("Can't merge a list with a non-list: " + key);

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
        throw new IllegalArgumentException(
                "Can't merge element of unknown type: " + key + ": " + yamlValue.getClass().getSimpleName());
    }
}
