package io.goodforgod.micronaut.openapi;


/**
 * Configuration for OpenAPI.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 19.12.2021
 */
public final class OpenAPISettings {

    private OpenAPISettings() {}

    /**
     * Prefix to use for all OpenAPI Library settings.
     */
    public static final String PREFIX = "openapi";

    /**
     * Default directory inside JAR to scan for OpenAPI files
     */
    public static final String DEFAULT_DIR = "META-INF/swagger";

    /**
     * Default URL for OpenAPI.
     */
    public static final String DEFAULT_OPENAPI_URL = "/openapi";

    /**
     * Default URL for Swagger UI.
     */
    public static final String DEFAULT_SWAGGER_UI_URL = "/swagger-ui";

    /**
     * Default URL for Rapidoc.
     */
    public static final String DEFAULT_RAPIDOC_URL = "/rapidoc";
}
