package io.goodforgod.micronaut.swagger.api.model;


import java.io.InputStream;
import java.net.URI;
import org.jetbrains.annotations.Nullable;


/**
 * Model for representing inner resource
 *
 * @author Anton Kurako (GoodforGod)
 * @since 18.12.2021
 */
public final class URIResource implements Resource {

    private final URI uri;

    private URIResource(URI uri) {
        this.uri = uri;
    }

    public static URIResource of(URI uri) {
        return new URIResource(uri);
    }

    /**
     * @return URI to resource
     */
    public URI getUri() {
        return uri;
    }

    /**
     * @return resource input stream
     */
    @Override
    public @Nullable InputStream getInputStream() {
        final String path = getUri().getPath();
        final InputStream stream = getClass().getClassLoader().getResourceAsStream(path);
        return (stream == null)
                ? getClass().getClassLoader().getResourceAsStream("/" + path)
                : stream;
    }

    @Override
    public void close() {
        // do nothing
    }
}
