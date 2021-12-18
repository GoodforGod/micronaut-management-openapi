package io.goodforgod.micronaut.openapi.service;


import io.goodforgod.micronaut.openapi.config.OpenAPIConfig;
import io.goodforgod.micronaut.openapi.model.BufferedResource;
import io.goodforgod.micronaut.openapi.model.Resource;
import io.goodforgod.micronaut.openapi.model.URIResource;
import io.goodforgod.micronaut.openapi.utils.ResourceUtils;
import io.micronaut.core.util.CollectionUtils;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;


/**
 * @author Anton Kurako (GoodforGod)
 * @since 20.9.2020
 */
@Singleton
public class OpenAPIProvider {

    private static final String MERGED_FILE = "openapi-merged.yml";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Collection<Resource> cachedResources = null;
    private Resource merged = null;

    private final OpenAPIConfig openAPIConfig;
    private final YamlMerger yamlMerger;

    @Inject
    public OpenAPIProvider(OpenAPIConfig openAPIConfig, YamlMerger yamlMerger) {
        this.openAPIConfig = openAPIConfig;
        this.yamlMerger = yamlMerger;
    }

    /**
     * @return current service resource
     */
    public Optional<Resource> getAny() {
        return getAll().stream().findFirst();
    }

    /**
     * @return merged swagger in there are any to merge
     */
    public Optional<Resource> getMerged() {
        if (merged != null) {
            return Optional.of(merged);
        }

        final Collection<Resource> resources = getAll();
        if (CollectionUtils.isEmpty(resources)) {
            logger.debug("No swagger files found for merging");
            return Optional.empty();
        }

        if (resources.size() == 1) {
            logger.debug("Found 1 swagger file, merge is not required");
            final Resource resource = resources.iterator().next();
            return Optional.of(resource);
        }

        final Map<Object, Object> mergedYaml = yamlMerger.merge(resources);
        if (CollectionUtils.isEmpty(mergedYaml)) {
            logger.debug("Merged swagger file is empty");
            return Optional.empty();
        }

        try {
            final Resource resource = getYamlAsResource(mergedYaml);
            return Optional.of(resource);
        } catch (Exception e) {
            final Resource directResource = getDirectResource(mergedYaml);
            return Optional.of(directResource);
        }
    }

    /**
     * @param yaml file to dump to file as merged one to cache it
     * @return resource with dumped merged YAML file
     */
    private Resource getYamlAsResource(@NotNull Map<Object, Object> yaml) {
        logger.debug("Writing OpenAPI file to: {}", MERGED_FILE);
        try (final Writer writer = new BufferedWriter(new FileWriter(MERGED_FILE))) {
            new Yaml().dump(yaml, writer);
            this.merged = URIResource.of(new URI(MERGED_FILE));
            return merged;
        } catch (Exception e) {
            try (final StringWriter writer = new StringWriter()) {
                new Yaml().dump(yaml, writer);
                final String value = writer.toString();
                this.merged = BufferedResource.of(value);
                return merged;
            } catch (Exception ex) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }
    }

    /**
     * @param yaml to dump directly to inputStream
     * @return resource with YAML as direct inputStream
     */
    private Resource getDirectResource(@NotNull Map<Object, Object> yaml) {
        logger.debug("Dumping file directly to input stream");
        try (final ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            try (final Writer writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {
                new Yaml().dump(yaml, writer);
                final String value = stream.toString(StandardCharsets.UTF_8);
                return BufferedResource.of(value);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * @return get all OpenAPI files as resources
     */
    public Collection<Resource> getAll() {
        if (cachedResources != null) {
            return cachedResources;
        }

        if (!openAPIConfig.getInclude().isEmpty()) {
            return openAPIConfig.getInclude().stream()
                    .map(path -> {
                        try {
                            final URL resource = OpenAPIProvider.class.getResource(path);
                            if (resource == null) {
                                throw new IllegalArgumentException("OpenAPI not found: " + path);
                            }

                            return resource.toURI();
                        } catch (URISyntaxException e) {
                            throw new IllegalArgumentException(e.getMessage());
                        }
                    })
                    .map(URIResource::of)
                    .collect(Collectors.toList());
        } else {
            final String directory = openAPIConfig.getDefaultDirectory();
            final Set<String> exclude = openAPIConfig.getExclude();
            this.cachedResources = ResourceUtils.getResources(directory, p -> p.endsWith(".yml") || p.endsWith(".yaml"))
                    .stream()
                    .filter(r -> exclude.stream().noneMatch(ex -> r.getURI().getPath().endsWith(ex)))
                    .collect(Collectors.toList());
            return cachedResources;
        }
    }
}
