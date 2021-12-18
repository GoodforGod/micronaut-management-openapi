package io.goodforgod.micronaut.openapi.model;


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
    public URI getURI() {
        return uri;
    }

    @Override
    public @Nullable InputStream getStream() {
        final String path = getURI().getPath();
        final InputStream stream = getClass().getClassLoader().getResourceAsStream(path);
        return (stream == null)
                ? getClass().getClassLoader().getResourceAsStream("/" + path)
                : stream;
        // try {
        // return ResourceUtils.readFileFromStream(inputStream);
        // } catch (IOException e) {
        // throw new IllegalArgumentException("Can't read file: " + path, e);
        // }
    }

    @Override
    public void close() {
        // do nothing
    }
}
