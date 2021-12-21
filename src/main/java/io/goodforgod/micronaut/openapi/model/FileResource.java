package io.goodforgod.micronaut.openapi.model;

import java.io.File;
import org.jetbrains.annotations.NotNull;

/**
 * Model for representing inner resource
 *
 * @author Anton Kurako (GoodforGod)
 * @since 18.12.2021
 */
public final class FileResource implements PathResource {

    private final File file;

    private FileResource(File file) {
        this.file = file;
    }

    public static FileResource of(@NotNull File file) {
        return new FileResource(file);
    }

    public File getFile() {
        return file;
    }

    @Override
    public @NotNull String getPath() {
        return file.toURI().getPath();
    }
}
