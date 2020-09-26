package micronaut.swagger.api.service;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.CollectionUtils;
import io.reactivex.Maybe;
import micronaut.swagger.api.config.SwaggerConfig;
import micronaut.swagger.api.model.Resource;
import micronaut.swagger.api.utils.ResourceUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Swagger loader service
 *
 * @author Anton Kurako (GoodforGod)
 * @since 20.9.2020
 */
@Requires(beans = SwaggerConfig.class)
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
    public Maybe<Resource> getSwagger() {
        return config.isMerge()
                ? getMergedSwagger()
                : getServiceSwagger();
    }

    /**
     * @return current service resource
     */
    public Maybe<Resource> getServiceSwagger() {
        return getSwaggers().stream()
                .max(Comparator.comparingLong(Resource::getCreated))
                .map(Maybe::just)
                .orElse(Maybe.empty());
    }

    /**
     * @return merged swagger in there are any to merge
     */
    public Maybe<Resource> getMergedSwagger() {
        if (merged != null)
            return Maybe.just(merged);

        final Collection<Resource> resources = getSwaggers();
        if (CollectionUtils.isEmpty(resources)) {
            logger.debug("No swagger files found for merging");
            return Maybe.empty();
        }

        if (resources.size() == 1) {
            logger.debug("Found 1 swagger file, merge is not required");
            final Resource resource = resources.iterator().next();
            return Maybe.just(resource);
        }

        final Map<Object, Object> mergedYaml = yamlMerger.merge(resources);
        if (CollectionUtils.isEmpty(mergedYaml)) {
            logger.debug("Merged swagger file is empty");
            return Maybe.empty();
        }

        try {
            final Resource resource = getFileResource(mergedYaml);
            return Maybe.just(resource);
        } catch (Exception e) {
            final Resource directResource = getDirectResource(mergedYaml);
            return Maybe.just(directResource);
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
            this.merged = new Resource(new URI(SWAGGER_MERGED), Instant.now().getEpochSecond());
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
            return new Resource(null, Instant.now().getEpochSecond()) {

                @Override
                public InputStream getInputStream() {
                    return new BufferedInputStream(new ByteArrayInputStream(stream.toByteArray()));
                }
            };
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * @return get swaggers as resources
     */
    public Collection<Resource> getSwaggers() {
        if (cachedResources != null)
            return cachedResources;

        this.cachedResources = ResourceUtils.getResources(SWAGGER_DIR, p -> p.endsWith(".yml") || p.endsWith(".yaml")).stream()
                .filter(r -> config.getExclude().stream().noneMatch(excluded -> r.getUri().getPath().endsWith(excluded)))
                .collect(Collectors.toList());
        return cachedResources;
    }
}
