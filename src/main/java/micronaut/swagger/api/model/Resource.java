package micronaut.swagger.api.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.net.URI;
import java.util.Objects;

/**
 * Model for representing inner resource
 *
 * @author Anton Kurako (GoodforGod)
 * @since 21.9.2020
 */
public class Resource {

    private final URI uri;
    private final long created;

    public Resource(@NotNull URI uri, long created) {
        this.uri = uri;
        this.created = created;
    }

    public @NotNull URI getUri() {
        return uri;
    }

    public long getCreated() {
        return created;
    }

    public @Nullable InputStream getInputStream() {
        final String path = getUri().getPath();
        final InputStream stream = getClass().getClassLoader().getResourceAsStream(path);
        return (stream == null)
                ? getClass().getClassLoader().getResourceAsStream("/" + path)
                : stream;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Resource resource = (Resource) o;
        return Objects.equals(uri, resource.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }

    @Override
    public String toString() {
        return "Resource{" +
                "uri=" + uri +
                ", created=" + created +
                '}';
    }
}
