package io.goodforgod.micronaut.openapi.model;

import java.net.URI;
import org.jetbrains.annotations.NotNull;

/**
 * Model for representing inner resource
 *
 * @author Anton Kurako (GoodforGod)
 * @since 18.12.2021
 */
public final class URIResource implements PathResource {

    private final URI uri;

    private URIResource(URI uri) {
        this.uri = uri;
    }

    public static URIResource of(@NotNull URI uri) {
        return new URIResource(uri);
    }

    @Override
    public @NotNull String getPath() {
        return uri.getPath();
    }
}
