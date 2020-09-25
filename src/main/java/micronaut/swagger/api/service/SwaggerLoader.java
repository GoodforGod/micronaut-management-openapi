package micronaut.swagger.api.service;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.CollectionUtils;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import micronaut.swagger.api.config.SwaggerConfig;
import micronaut.swagger.api.model.Resource;
import micronaut.swagger.api.utils.ResourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.net.URI;
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
        return getSwaggersResources().stream()
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

        final Collection<Resource> resources = getSwaggersResources();
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

        try (final Writer writer = new BufferedWriter(new FileWriter(SWAGGER_MERGED))) {
            new Yaml().dump(mergedYaml, writer);
            this.merged = new Resource(new URI(SWAGGER_MERGED), Instant.now().getEpochSecond());
            logger.debug("Merged swagger file written to: {}", SWAGGER_MERGED);
            return Maybe.just(merged);
        } catch (IOException e) {
            // TODO handle when can't write have to dump to inputstream directly
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * @return swaggers as flowable
     */
    public Flowable<Resource> getSwaggers() {
        return Flowable.fromIterable(getSwaggersResources());
    }

    /**
     * @return get swaggers as resources
     */
    private Collection<Resource> getSwaggersResources() {
        if (cachedResources != null)
            return cachedResources;

        this.cachedResources = ResourceUtils.getResources(SWAGGER_DIR, p -> p.endsWith(".yml") || p.endsWith(".yaml")).stream()
                .filter(r -> config.getExclude().stream().noneMatch(excluded -> r.getUri().getPath().endsWith(excluded)))
                .collect(Collectors.toList());
        return cachedResources;
    }
}
