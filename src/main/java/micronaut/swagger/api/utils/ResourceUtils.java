package micronaut.swagger.api.utils;

import io.micronaut.core.util.ArrayUtils;
import micronaut.swagger.api.model.Resource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * Utils for extraction resources inside JAR and outside JAR
 *
 * @author Anton Kurako (GoodforGod)
 * @since 23.9.2020
 */
public class ResourceUtils {

    private static final Logger logger = LoggerFactory.getLogger(ResourceUtils.class);

    private ResourceUtils() {}

    /**
     *
     * @param path under which to look for resources
     * @return resources under given path
     */
    public static List<Resource> getResources(@NotNull String path) {
        return getResources(path, p -> true);
    }

    /**
     * @param path          under which to look for resources
     * @param pathPredicate predicate to validate paths
     * @return resources under given path
     */
    public static List<Resource> getResources(@NotNull String path, @NotNull Predicate<String> pathPredicate) {
        logger.debug("Looking for files inside JAR in path: {}", path);
        try {
            final URL url = ResourceUtils.class.getClassLoader().getResource(path);
            if (url == null)
                throw new URISyntaxException("null", "URL is nullable");

            final String jarPath = url.getPath()
                    .replaceFirst("[.]jar[!].*", ".jar")
                    .replaceFirst("file:", "")
                    .replace(" ", "\\ ");

            logger.debug("Opening JAR in path: {}", jarPath);
            try (final JarFile jarFile = new JarFile(jarPath)) {
                final Enumeration<JarEntry> entries = jarFile.entries();
                final List<Resource> resources = new ArrayList<>();

                while (entries.hasMoreElements()) {
                    final JarEntry entry = entries.nextElement();
                    if (entry.getRealName().startsWith(path) && pathPredicate.test(entry.getRealName())) {
                        final URI uri = new URI(entry.getName());
                        final FileTime creationTime = (entry.getCreationTime() == null)
                                ? entry.getLastModifiedTime()
                                : entry.getCreationTime();

                        logger.debug("Found files at path '{}' with creation time '{}'", uri, creationTime);
                        resources.add(new Resource(uri, creationTime.to(TimeUnit.SECONDS)));
                    }
                }

                logger.debug("Found '{}' files inside JAR", resources.size());
                return resources;
            }
        } catch (IOException | URISyntaxException e) {
            final String filePath = "/" + path;
            logger.debug("Can not open JAR file, looking for files outside JAR in path: {}", filePath);
            final File[] files = new File(ResourceUtils.class.getResource(filePath).getPath()).listFiles();
            if (ArrayUtils.isEmpty(files)) {
                logger.debug("No swaggers found outside JAR in path: {}", filePath);
                return Collections.emptyList();
            }

            logger.debug("Found '{}' files outside JAR in path: {}", files.length, filePath);
            return Arrays.stream(files)
                    .map(f -> new Resource(f.toURI(), f.lastModified()))
                    .collect(Collectors.toList());
        }
    }
}
