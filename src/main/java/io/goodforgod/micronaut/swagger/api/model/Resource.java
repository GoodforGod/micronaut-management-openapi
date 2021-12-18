package io.goodforgod.micronaut.swagger.api.model;


import java.io.InputStream;
import org.jetbrains.annotations.Nullable;


/**
 * @author Anton Kurako (GoodforGod)
 * @since 18.12.2021
 */
public interface Resource extends AutoCloseable {

    @Nullable
    InputStream getInputStream();
}
