package io.goodforgod.micronaut.openapi.service;

import io.goodforgod.micronaut.openapi.config.OpenAPIConfig;
import io.goodforgod.micronaut.openapi.model.*;
import io.goodforgod.micronaut.openapi.utils.ResourceUtils;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.*;
import java.net.URI;
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
@Introspected
@Singleton
public class OpenAPIProvider {

    private static final String MERGED_FILE = "openapi-merged.yml";

    private static final Logger logger = LoggerFactory.getLogger(OpenAPIProvider.class);

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
    @NotNull
    public Optional<Resource> getAny() {
        return getAll().stream().findFirst();
    }

    /**
     * @return merged OpenAPI
     */
    @NotNull
    public Optional<Resource> getMerged() {
        if (merged != null) {
            return Optional.of(merged);
        }

        final Collection<Resource> resources = getAll();
        if (CollectionUtils.isEmpty(resources)) {
            logger.debug("No OpenAPI files found for merging");
            return Optional.empty();
        }

        if (resources.size() == 1) {
            logger.debug("Found '1' OpenAPI file, merge is not required");
            final Resource resource = resources.iterator().next();
            return Optional.of(resource);
        }

        logger.debug("Merging '{}' OpenAPI files into one...", resources.size());
        final Map<Object, Object> mergedYaml = yamlMerger.merge(resources);
        if (CollectionUtils.isEmpty(mergedYaml)) {
            logger.debug("Merged OpenAPI file is empty");
            return Optional.empty();
        }

        logger.debug("Finished OpenAPI files merging");
        try {
            final Resource fileResource = getYamlAsFileResource(mergedYaml);
            this.merged = fileResource;
            return Optional.of(fileResource);
        } catch (Exception e) {
            final Resource bufferedResource = getYamlAsBufferedResource(mergedYaml);
            this.merged = bufferedResource;
            return Optional.of(bufferedResource);
        }
    }

    /**
     * @param yaml file to dump to file as merged one to cache it
     * @return resource with dumped merged YAML file
     */
    private Resource getYamlAsFileResource(@NotNull Map<Object, Object> yaml) {
        logger.debug("Trying writing OpenAPI file to: {}", MERGED_FILE);
        final File file = new File(MERGED_FILE);
        try (final Writer writer = new BufferedWriter(new FileWriter(file))) {
            new Yaml().dump(yaml, writer);
            logger.debug("Finished OpenAPI file write to: {}", MERGED_FILE);
            return FileResource.of(file);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * @param yaml to dump directly to inputStream
     * @return resource with YAML as direct buffered resource
     */
    private Resource getYamlAsBufferedResource(@NotNull Map<Object, Object> yaml) {
        logger.debug("Trying writing OpenAPI file to BufferedResource");
        try (final StringWriter writer = new StringWriter()) {
            new Yaml().dump(yaml, writer);
            final String value = writer.toString();
            return BufferedResource.of(value);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    /**
     * @return get all OpenAPI files as resources
     */
    @NotNull
    public Collection<Resource> getAll() {
        if (cachedResources != null) {
            return cachedResources;
        }

        if (!openAPIConfig.getInclude().isEmpty()) {
            this.cachedResources = openAPIConfig.getInclude().stream()
                    .map(OpenAPIProvider::getDirectResource)
                    .collect(Collectors.toList());
        } else {
            final String directory = openAPIConfig.getDefaultDirectory();
            final Set<String> exclude = openAPIConfig.getExclude();
            this.cachedResources = ResourceUtils.getResources(directory, p -> p.endsWith(".yml") || p.endsWith(".yaml"))
                    .stream()
                    .filter(r -> exclude.stream().noneMatch(ex -> r.getPath().endsWith(ex)))
                    .collect(Collectors.toList());
        }

        logger.debug("Caching '{}' resources", cachedResources.size());
        return cachedResources;
    }

    private static Resource getDirectResource(@NotNull String path) {
        logger.debug("Loading resource as direct for path: {}", path);
        InputStream stream = OpenAPIProvider.class.getClassLoader().getResourceAsStream(path);
        if (stream == null) {
            stream = OpenAPIProvider.class.getClassLoader().getResourceAsStream("/" + path);
            if (stream == null) {
                throw new HttpStatusException(HttpStatus.NOT_IMPLEMENTED, "Can't read direct OpenAPI file: " + path);
            } else {
                return URIResource.of(URI.create("/" + path));
            }
        } else {
            return URIResource.of(URI.create(path));
        }
    }
}
