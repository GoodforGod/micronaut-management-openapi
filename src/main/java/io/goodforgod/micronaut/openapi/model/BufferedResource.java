package io.goodforgod.micronaut.openapi.model;


import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.jetbrains.annotations.NotNull;


/**
 * Model for representing inner resource
 *
 * @author Anton Kurako (GoodforGod)
 * @since 18.12.2021
 */
public final class BufferedResource implements Resource {

    private final String value;

    private BufferedResource(String value) {
        this.value = value;
    }

    public static BufferedResource of(@NotNull String value) {
        return new BufferedResource(value);
    }

    /**
     * @return resource input stream
     */
    @Override
    public @NotNull InputStream getStream() {
        return new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8));
    }

    public String getValue() {
        return value;
    }

    @Override
    public void close() throws Exception {
        // do nothing
    }
}