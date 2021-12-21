package io.goodforgod.micronaut.openapi.model;

import java.net.URL;
import org.jetbrains.annotations.NotNull;

/**
 * Model for representing inner resource
 *
 * @author Anton Kurako (GoodforGod)
 * @since 18.12.2021
 */
public final class URLResource implements PathResource {

    private final URL url;

    private URLResource(URL url) {
        this.url = url;
    }

    public static URLResource of(@NotNull URL url) {
        return new URLResource(url);
    }

    @Override
    public @NotNull String getPath() {
        return url.getPath();
    }
}
