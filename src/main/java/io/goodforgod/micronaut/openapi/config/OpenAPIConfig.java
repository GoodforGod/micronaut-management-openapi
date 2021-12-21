package io.goodforgod.micronaut.openapi.config;

import io.goodforgod.micronaut.openapi.OpenAPISettings;
import io.goodforgod.micronaut.openapi.controller.OpenAPIController;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.util.CollectionUtils;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

/**
 * Configuration for OpenAPI
 *
 * @author Anton Kurako (GoodforGod)
 * @since 20.9.2020
 */
@ConfigurationProperties(OpenAPISettings.PREFIX)
public class OpenAPIConfig {

    /**
     * Enables {@link OpenAPIController} controller
     */
    private boolean enabled = true;

    /**
     * Allow merging multiple OpenAPI files into one
     */
    private boolean merge = false;

    /**
     * Allow change {@link OpenAPIController} controller path
     */
    private String path = OpenAPISettings.DEFAULT_OPENAPI_URL;

    /**
     * Default directory inside JAR to scan for OpenAPI files
     * 
     * @see io.goodforgod.micronaut.openapi.service.OpenAPIProvider
     */
    private String defaultDirectory = OpenAPISettings.DEFAULT_DIR;

    /**
     * Exclude listed OpenAPI files (if {@link #include} is not specified)
     */
    private Set<String> exclude;

    /**
     * Include only listed OpenAPI files (ignores {@link #exclude})
     */
    private Set<String> include;

    public boolean isEnabled() {
        return enabled;
    }

    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isMerge() {
        return merge;
    }

    void setMerge(boolean merge) {
        this.merge = merge;
    }

    public String getPath() {
        return path;
    }

    void setPath(String path) {
        this.path = path;
    }

    public String getDefaultDirectory() {
        return defaultDirectory;
    }

    void setDefaultDirectory(String defaultDirectory) {
        this.defaultDirectory = defaultDirectory;
    }

    public @NotNull Set<String> getExclude() {
        return CollectionUtils.isEmpty(exclude)
                ? Collections.emptySet()
                : exclude;
    }

    void setExclude(List<String> exclude) {
        this.exclude = new HashSet<>(exclude);
    }

    public @NotNull Set<String> getInclude() {
        return CollectionUtils.isEmpty(include)
                ? Collections.emptySet()
                : include;
    }

    void setInclude(List<String> include) {
        this.include = new HashSet<>(include);
    }
}
