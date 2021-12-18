package io.goodforgod.micronaut.swagger.api.config;


import io.goodforgod.micronaut.swagger.api.SwaggerSettings;
import io.micronaut.context.annotation.ConfigurationProperties;


/**
 * Configuration for Swagger UI
 *
 * @author Anton Kurako (GoodforGod)
 * @since 18.12.2021
 */
@ConfigurationProperties(SwaggerSettings.PREFIX + ".swagger-ui")
public class SwaggerUIConfig {

    private boolean enabled = true;

    private String path = SwaggerSettings.DEFAULT_SWAGGER_UI_URL;

    public String getPath() {
        return path;
    }

    void setPath(String path) {
        this.path = path;
    }

    public boolean isEnabled() {
        return enabled;
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
