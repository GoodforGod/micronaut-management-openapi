package micronaut.swagger.api.model;

import java.io.InputStream;
import java.net.URI;
import java.util.Objects;

/**
 * Model for representing swagger file
 *
 * @author Anton Kurako (GoodforGod)
 * @since 21.9.2020
 */
public class Swagger {

    private boolean merged = false;
    private final URI uri;
    private final long created;
    private InputStream stream;

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

    public InputStream getStream() {
        return stream;
    }

    public Swagger withStream(InputStream stream) {
        this.stream = stream;
        return this;
    }

    public boolean isMerged() {
        return merged;
    }

    public Swagger asMerged() {
        this.merged = true;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
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
