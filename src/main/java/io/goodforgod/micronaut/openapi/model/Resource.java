package io.goodforgod.micronaut.openapi.model;

import java.io.InputStream;
import org.jetbrains.annotations.Nullable;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 18.12.2021
 */
public interface Resource {

    /**
     * @return resource data as input stream
     */
    @Nullable
    InputStream getStream();
}
