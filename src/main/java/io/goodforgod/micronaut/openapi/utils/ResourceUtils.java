package io.goodforgod.micronaut.openapi.utils;


import io.goodforgod.micronaut.openapi.model.FileResource;
import io.goodforgod.micronaut.openapi.model.PathResource;
import io.goodforgod.micronaut.openapi.model.URIResource;
import io.micronaut.core.io.IOUtils;
import io.micronaut.core.util.ArrayUtils;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Utils for extraction resources inside JAR and outside JAR
 *
 * @author Anton Kurako (GoodforGod)
 * @since 23.9.2020
 */
public final class ResourceUtils {

    private static final Logger logger = LoggerFactory.getLogger(ResourceUtils.class);

    private ResourceUtils() {}

    /**
     * @param path under which to look for resources
     * @return resources under given path
     */
    public static List<PathResource> getResources(@NotNull String path) {
        return getResources(path, p -> true);
    }

    /**
     * @param path          under which to look for resources
     * @param pathPredicate predicate to validate paths
     * @return resources under given path
     */
    public static List<PathResource> getResources(@NotNull String path,
                                                  @NotNull Predicate<String> pathPredicate) {
        logger.debug("Looking for files inside JAR with path: {}", path);
        try {
            final URL url = ResourceUtils.class.getClassLoader().getResource(path);
            if (url == null) {
                throw new IllegalArgumentException("URL is nullable for path: " + path);
            }

            final String jarPath = url.getPath()
                    .replaceFirst("[.]jar[!].*", ".jar")
                    .replaceFirst("file:", "")
                    .replace(" ", "\\ ");

            logger.debug("Opening JAR with path: {}", jarPath);
            try (final JarFile jarFile = new JarFile(jarPath)) {
                final Enumeration<JarEntry> entries = jarFile.entries();
                final List<PathResource> resources = new ArrayList<>();

                while (entries.hasMoreElements()) {
                    final JarEntry entry = entries.nextElement();
                    if (entry.getRealName().startsWith(path) && pathPredicate.test(entry.getRealName())) {
                        final URI uri = new URI(entry.getName());
                        logger.debug("Found files at path: {}", uri);
                        resources.add(URIResource.of(uri));
                    }
                }

                logger.debug("Found '{}' files inside JAR", resources.size());
                return resources;
            }
        } catch (IOException | URISyntaxException e) {
            final String filePath = "/" + path;
            logger.debug("Can not open JAR file, looking for files outside JAR with path: {}", filePath);

            final URL resource = ResourceUtils.class.getResource(filePath);
            if (resource == null) {
                return Collections.emptyList();
            }

            final File[] files = new File(resource.getPath()).listFiles();
            if (ArrayUtils.isEmpty(files)) {
                logger.debug("No swaggers found outside JAR with path: {}", filePath);
                return Collections.emptyList();
            }

            logger.debug("Found '{}' files outside JAR with path: {}", files.length, filePath);
            return Arrays.stream(files)
                    .map(FileResource::of)
                    .collect(Collectors.toList());
        }
    }

    public static Optional<InputStream> getFileAsStream(@NotNull String path) {
        return Optional.ofNullable(ResourceUtils.class.getResourceAsStream(path))
                .or(() -> Optional.ofNullable(ResourceUtils.class.getResourceAsStream("/" + path)));
    }

    public static Optional<String> getFileAsString(@NotNull String path) {
        return ResourceUtils.getFileAsStream(path)
                .map(s -> {
                    try {
                        return readFileFromStream(s);
                    } catch (IOException e) {
                        throw new IllegalArgumentException("Can't read file: " + path, e);
                    }
                });
    }

    public static String readFileFromStream(InputStream stream) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            return IOUtils.readText(in);
        }
    }
}
