package micronaut.swagger.api;

/**
 * Settings for Swagger.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 20.9.2020
 */
public interface SwaggerSettings {

    /**
     * Prefix to use for all Swagger Library settings.
     */
    String PREFIX = "swagger";

    /**
     * Default URL for Swagger.
     */
    String DEFAULT_SWAGGER_URL = "/swagger";

    /**
     * Default URL for Swagger UI.
     */
    String DEFAULT_SWAGGER_UI_URL = "/swagger-ui";

    /**
     * Default URL for Rapidoc.
     */
    String DEFAULT_RAPIDOC_URL = "/rapidoc";
}
