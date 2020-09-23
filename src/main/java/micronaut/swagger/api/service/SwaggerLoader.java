package micronaut.swagger.api.service;

import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.ArrayUtils;
import io.micronaut.core.util.CollectionUtils;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import micronaut.swagger.api.config.SwaggerConfig;
import micronaut.swagger.api.model.Swagger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
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

    private Collection<Swagger> cachedSwaggers = null;
    private Swagger merged = null;

    private final SwaggerConfig config;
    private final YamlMerger yamlMerger;

    @Inject
    public SwaggerLoader(SwaggerConfig config, YamlMerger yamlMerger) {
        this.config = config;
        this.yamlMerger = yamlMerger;
    }

    public Maybe<Swagger> getSwagger() {
        return config.isMerge()
                ? getMergedSwagger()
                : getServiceSwagger();
    }

    public Maybe<Swagger> getServiceSwagger() {
        return getSwaggersCollection().stream().max(Comparator.comparingLong(Swagger::getCreated))
                .map(Maybe::just)
                .orElse(Maybe.empty());
    }

    public Maybe<Swagger> getMergedSwagger() {
        if (merged != null)
            return Maybe.just(merged);

        final Collection<Swagger> swaggers = getSwaggersCollection();
        if (CollectionUtils.isEmpty(swaggers)) {
            logger.debug("No swagger files found for merging");
            return Maybe.empty();
        }

        if(swaggers.size() == 1) {
            logger.debug("Found 1 swagger file, merge is not required");
            return Maybe.just(swaggers.iterator().next());
        }

        final Map<Object, Object> mergedYaml = yamlMerger.merge(swaggers);
        if (CollectionUtils.isEmpty(mergedYaml)) {
            logger.debug("Merged swagger file is empty");
            return Maybe.empty();
        }

        try (final Writer writer = new BufferedWriter(new FileWriter(SWAGGER_MERGED))) {
            new Yaml().dump(mergedYaml, writer);
            this.merged = new Swagger(new URI(SWAGGER_MERGED), Instant.now().getEpochSecond()).asMerged();
            logger.debug("Merged swagger file written to: {}", SWAGGER_MERGED);
            return Maybe.just(merged);
        } catch (IOException e) {
            // TODO handle when can't write have to dump to inputstream directly
            throw new IllegalArgumentException(e.getMessage());
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Flowable<Swagger> getSwaggers() {
        return Flowable.fromIterable(getSwaggersCollection());
    }

    private Collection<Swagger> getSwaggersCollection() {
        if (cachedSwaggers != null)
            return cachedSwaggers;

        logger.debug("Looking for swaggers inside JAR in path: {}", SWAGGER_DIR);
        try {
            final URL url = getClass().getClassLoader().getResource(SWAGGER_DIR);
            if(url == null)
                throw new URISyntaxException("null", "URL is nullable");

            final String jarPath = url.getPath()
                    .replaceFirst("[.]jar[!].*", ".jar")
                    .replaceFirst("file:", "")
                    .replace(" ", "\\ ");

            logger.debug("Opening JAR in path: {}", jarPath);
            try (final JarFile jarFile = new JarFile(jarPath)) {
                final Enumeration<JarEntry> entries = jarFile.entries();
                final List<Swagger> swaggers = new ArrayList<>();

                while (entries.hasMoreElements()) {
                    final JarEntry entry = entries.nextElement();
                    if (entry.getName().endsWith(".yml") || entry.getName().endsWith(".yaml")) {
                        final URI uri = new URI(entry.getName());
                        final FileTime creationTime = (entry.getCreationTime() == null)
                                ? entry.getLastModifiedTime()
                                : entry.getCreationTime();

                        logger.debug("Found swagger at path '{}' with creation time '{}'", uri, creationTime);
                        swaggers.add(new Swagger(uri, creationTime.to(TimeUnit.SECONDS)));
                    }
                }

                logger.debug("Found '{}' swaggers inside JAR", swaggers.size());
                this.cachedSwaggers = swaggers;
                return swaggers;
            }
        } catch (IOException | URISyntaxException e) {
            final String path = "/" + SWAGGER_DIR;
            logger.debug("Can not open JAR file, looking for swaggers outside JAR in path: {}", path);
            final File[] files = new File(getClass().getResource(path).getPath()).listFiles();
            if (ArrayUtils.isEmpty(files)) {
                logger.debug("No swaggers found outside JAR in path: {}", path);
                return Collections.emptyList();
            }

            logger.debug("Found '{}' swaggers outside JAR in path: {}", files.length, path);
            return Arrays.stream(files)
                    .map(f -> new Swagger(f.toURI(), f.lastModified()))
                    .collect(Collectors.toList());
        }
    }
}
