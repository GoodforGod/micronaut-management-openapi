package micronaut.swagger.api.service;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.CollectionUtils;
import micronaut.swagger.api.config.SwaggerConfig;
import micronaut.swagger.api.model.Swagger;
import org.yaml.snakeyaml.Yaml;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Description in progress
 *
 * @author Anton Kurako (GoodforGod)
 * @since 20.9.2020
 */
@Requires(beans = SwaggerConfig.class)
@Singleton
public class SwaggerLoader {

    private static final String SWAGGER_DIR = "META-INF/swagger";
    private static final String SWAGGER_MERGED = "swagger-merged.yml";

    private Collection<Swagger> cachedSwaggers = null;
    private Swagger merged = null;

    private final YamlMerger yamlMerger;

    @Inject
    public SwaggerLoader(YamlMerger yamlMerger) {
        this.yamlMerger = yamlMerger;
    }

    public Optional<Swagger> getServiceSwagger() {
        return getSwaggers().stream().min(Comparator.comparingLong(Swagger::getCreated));
    }

    public Optional<Swagger> getMergedSwagger() {
        if(merged != null)
            return Optional.of(merged);

        final Collection<Swagger> swaggers = getSwaggers();
        final Map<Object, Object> mergedYaml = yamlMerger.merge(swaggers);
        if(CollectionUtils.isEmpty(mergedYaml))
            return Optional.empty();

        try (final Writer writer = new BufferedWriter(new FileWriter(SWAGGER_MERGED))) {
            new Yaml().dump(mergedYaml, writer);
            this.merged = new Swagger(new File(SWAGGER_MERGED).toURI(), Instant.now().getEpochSecond());
            return Optional.of(merged);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Collection<Swagger> getSwaggers() {
        if (cachedSwaggers != null)
            return cachedSwaggers;

        try (final JarFile jarFile = new JarFile(SWAGGER_DIR)) {
            final Enumeration<JarEntry> entries = jarFile.entries();
            final List<Swagger> swaggers = new ArrayList<>();

            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                if (entry.getName().endsWith(".yml") || entry.getName().endsWith(".yaml")) {
                    swaggers.add(new Swagger(new URI(entry.getName()), entry.getCreationTime().to(TimeUnit.SECONDS)));
                }
            }

            this.cachedSwaggers = swaggers;
            return swaggers;
        } catch (IOException | URISyntaxException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
