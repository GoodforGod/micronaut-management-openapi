package io.goodforgod.micronaut.swagger.api.model;


import java.io.InputStream;
import org.jetbrains.annotations.NotNull;


/**
 * Model for representing inner resource
 *
 * @author Anton Kurako (GoodforGod)
 * @since 18.12.2021
 */
public final class InputStreamResource implements Resource {

    private final InputStream inputStream;

    private InputStreamResource(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public static InputStreamResource of(@NotNull InputStream stream) {
        return new InputStreamResource(stream);
    }

    /**
     * @return resource input stream
     */
    public @NotNull InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public void close() throws Exception {
        if (inputStream != null) {
            inputStream.close();
        }
    }
}
