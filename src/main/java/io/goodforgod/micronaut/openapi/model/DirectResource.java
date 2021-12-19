package io.goodforgod.micronaut.openapi.model;


import java.io.InputStream;
import org.jetbrains.annotations.NotNull;


/**
 * Model for representing inner resource
 *
 * @author Anton Kurako (GoodforGod)
 * @since 18.12.2021
 */
public final class DirectResource implements PathResource {

    private final String path;

    private DirectResource(String path) {
        this.path = path;
    }

    public static DirectResource of(@NotNull String path) {
        return new DirectResource(path);
    }

    @Override
    public @NotNull String getPath() {
        return path;
    }

    @Override
    public @NotNull InputStream getStream() {
        return getClass().getClassLoader().getResourceAsStream(path);
    }

    @Override
    public void close() throws Exception {
        // do nothing
    }
}
