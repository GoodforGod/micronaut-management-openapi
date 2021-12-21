package io.goodforgod.micronaut.openapi.config;

import io.goodforgod.micronaut.openapi.OpenAPISettings;
import io.goodforgod.micronaut.openapi.controller.SwaggerUIController;
import io.micronaut.context.annotation.ConfigurationProperties;

/**
 * Configuration for Swagger UI
 *
 * @author Anton Kurako (GoodforGod)
 * @since 18.12.2021
 */
@ConfigurationProperties(OpenAPISettings.PREFIX + ".swagger-ui")
public class SwaggerUIConfig {

    /**
     * Enable Swagger-UI exposure {@link SwaggerUIController}
     */
    private boolean enabled = true;

    /**
     * Path for Swagger-UI exposure
     */
    private String path = OpenAPISettings.DEFAULT_SWAGGER_UI_URL;

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
