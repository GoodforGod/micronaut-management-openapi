package io.goodforgod.micronaut.swagger.api.config;


import io.goodforgod.micronaut.swagger.api.SwaggerSettings;
import io.micronaut.context.annotation.ConfigurationProperties;


/**
 * Configuration for Rapidoc
 *
 * @author Anton Kurako (GoodforGod)
 * @since 18.12.2021
 */
@ConfigurationProperties(SwaggerSettings.PREFIX + ".rapidoc")
public class RapidocConfig {

    private boolean enabled = false;

    private String path = SwaggerSettings.DEFAULT_RAPIDOC_URL;

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
