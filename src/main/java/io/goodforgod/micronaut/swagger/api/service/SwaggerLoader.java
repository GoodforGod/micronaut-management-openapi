package io.goodforgod.micronaut.swagger.api.service;


import io.goodforgod.micronaut.swagger.api.config.SwaggerConfig;
import io.goodforgod.micronaut.swagger.api.model.InputStreamResource;
import io.goodforgod.micronaut.swagger.api.model.Resource;
import io.goodforgod.micronaut.swagger.api.model.URIResource;
import io.goodforgod.micronaut.swagger.api.utils.ResourceUtils;
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
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import reactor.core.publisher.Mono;


/**
 * @author Anton Kurako (GoodforGod)
 * @since 20.9.2020
 */
@Singleton
public class SwaggerLoader {

    private static final String SWAGGER_DIR = "META-INF/swagger";
    private static final String SWAGGER_MERGED = "swagger-merged.yml";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Collection<Resource> cachedResources = null;
    private Resource merged = null;

    private final SwaggerConfig config;
    private final YamlMerger yamlMerger;

    @Inject
    public SwaggerLoader(SwaggerConfig config, YamlMerger yamlMerger) {
        this.config = config;
        this.yamlMerger = yamlMerger;
    }

    /**
     * @return swagger resource as specified per configuration
     */
    public Mono<Resource> getSwagger() {
        return config.isMerge()
                ? getMergedSwagger()
                : getFirstSwagger();
    }

    /**
     * @return current service resource
     */
    public Mono<Resource> getFirstSwagger() {
        return getSwaggers().stream().findFirst()
                .map(Mono::just)
                .orElse(Mono.empty());
    }

    /**
     * @return merged swagger in there are any to merge
     */
    public Mono<Resource> getMergedSwagger() {
        if (merged != null) {
            return Mono.just(merged);
        }

        final Collection<Resource> resources = getSwaggers();
        if (CollectionUtils.isEmpty(resources)) {
            logger.debug("No swagger files found for merging");
            return Mono.empty();
        }

        if (resources.size() == 1) {
            logger.debug("Found 1 swagger file, merge is not required");
            final Resource resource = resources.iterator().next();
            return Mono.just(resource);
        }

        final Map<Object, Object> mergedYaml = yamlMerger.merge(resources);
        if (CollectionUtils.isEmpty(mergedYaml)) {
            logger.debug("Merged swagger file is empty");
            return Mono.empty();
        }

        try {
            final Resource resource = getFileResource(mergedYaml);
            return Mono.just(resource);
        } catch (Exception e) {
            final Resource directResource = getDirectResource(mergedYaml);
            return Mono.just(directResource);
        }
    }

    /**
     * @param yaml file to dump to file as merged one to cache it
     * @return resource with dumped merged YAML file
     */
    private Resource getFileResource(@NotNull Map<Object, Object> yaml) {
        logger.debug("Merged swagger file written to: {}", SWAGGER_MERGED);
        try (final Writer writer = new BufferedWriter(new FileWriter(SWAGGER_MERGED))) {
            new Yaml().dump(yaml, writer);
            this.merged = URIResource.of(new URI(SWAGGER_MERGED));
            return merged;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * @param yaml to dump directly to inputStream
     * @return resource with YAML as direct inputStream
     */
    private Resource getDirectResource(@NotNull Map<Object, Object> yaml) {
        logger.debug("Dumping file directly to input stream");
        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try (final Writer writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)) {
            new Yaml().dump(yaml, writer);
            return InputStreamResource.of(new BufferedInputStream(new ByteArrayInputStream(stream.toByteArray())));
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * @return get swaggers as resources
     */
    public Collection<Resource> getSwaggers() {
        if (cachedResources != null) {
            return cachedResources;
        }

        if (!config.getInclude().isEmpty()) {
            return config.getInclude().stream()
                    .map(path -> {
                        try {
                            final URL resource = SwaggerLoader.class.getResource(path);
                            if (resource == null) {
                                throw new IllegalArgumentException("Swagger not found: " + path);
                            }

                            return resource.toURI();
                        } catch (URISyntaxException e) {
                            throw new IllegalArgumentException(e.getMessage());
                        }
                    })
                    .map(URIResource::of)
                    .collect(Collectors.toList());
        } else {
            this.cachedResources = ResourceUtils.getResources(SWAGGER_DIR, p -> p.endsWith(".yml") || p.endsWith(".yaml"))
                    .stream()
                    .filter(r -> config.getExclude().stream().noneMatch(excluded -> r.getUri().getPath().endsWith(excluded)))
                    .collect(Collectors.toList());
        }

        return cachedResources;
    }
}
