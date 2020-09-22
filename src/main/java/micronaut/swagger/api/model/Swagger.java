package micronaut.swagger.api.model;

import java.net.URI;
import java.nio.file.attribute.FileTime;
import java.util.Objects;

/**
 * Model for representing swagger file
 *
 * @author Anton Kurako (GoodforGod)
 * @since 21.9.2020
 */
public class Swagger {

    private final URI uri;
    private final long created;

    public Swagger(URI uri, long created) {
        this.uri = uri;
        this.created = created;
    }

    public URI getUri() {
        return uri;
    }

    public long getCreated() {
        return created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Swagger swagger = (Swagger) o;
        return Objects.equals(uri, swagger.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }

    @Override
    public String toString() {
        return "Swagger{" +
                "uri=" + uri +
                ", created=" + created +
                '}';
    }
}
