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

    String DEFAULT_SWAGGER_URL = "/swagger";

    String DEFAULT_SWAGGER_UI_URL = "/swagger-ui";

    String DEFAULT_RAPIDOC_URL = "/rapidoc";
}
