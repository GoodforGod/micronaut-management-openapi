package io.goodforgod.micronaut.swagger.api;


/**
 * Settings for Swagger.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 20.9.2020
 */
public final class SwaggerSettings {

    private SwaggerSettings() {}

    /**
     * Prefix to use for all Swagger Library settings.
     */
    public static final String PREFIX = "swagger";

    /**
     * Default URL for Swagger.
     */
    public static final String DEFAULT_SWAGGER_URL = "/swagger";

    /**
     * Default URL for Swagger UI.
     */
    public static final String DEFAULT_SWAGGER_UI_URL = "/swagger-ui";

    /**
     * Default URL for Rapidoc.
     */
    public static final String DEFAULT_RAPIDOC_URL = "/rapidoc";
}
