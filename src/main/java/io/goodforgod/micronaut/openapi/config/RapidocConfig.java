package io.goodforgod.micronaut.openapi.config;

import io.goodforgod.micronaut.openapi.OpenAPISettings;
import io.goodforgod.micronaut.openapi.controller.RapidocController;
import io.micronaut.context.annotation.ConfigurationProperties;

/**
 * Configuration for Rapidoc
 *
 * @author Anton Kurako (GoodforGod)
 * @since 18.12.2021
 */
@ConfigurationProperties(OpenAPISettings.PREFIX + ".rapidoc")
public class RapidocConfig {

    private boolean enabled = false;
    private String path = OpenAPISettings.DEFAULT_RAPIDOC_URL;

    public String getPath() {
        return path;
    }

    /**
     * Rapidoc exposure path
     */
    void setPath(String path) {
        this.path = path;
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Enables Rapidoc exposure via {@link RapidocController} controller
     */
    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
