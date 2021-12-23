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

    private boolean enabled = true;
    private boolean merge = false;
    private String path = OpenAPISettings.DEFAULT_OPENAPI_URL;
    private String defaultDirectory = OpenAPISettings.DEFAULT_DIR;
    private Set<String> exclude;
    private Set<String> include;

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Enables {@link OpenAPIController} controller
     */
    void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isMerge() {
        return merge;
    }

    /**
     * Allow merging multiple OpenAPI files into one
     */
    void setMerge(boolean merge) {
        this.merge = merge;
    }

    public String getPath() {
        return path;
    }

    /**
     * Allow change {@link OpenAPIController} controller path
     */
    void setPath(String path) {
        this.path = path;
    }

    public String getDefaultDirectory() {
        return defaultDirectory;
    }

    /**
     * Default directory inside JAR to scan for OpenAPI files
     *
     * @see io.goodforgod.micronaut.openapi.service.OpenAPIProvider
     */
    void setDefaultDirectory(String defaultDirectory) {
        this.defaultDirectory = defaultDirectory;
    }

    public @NotNull Set<String> getExclude() {
        return CollectionUtils.isEmpty(exclude)
                ? Collections.emptySet()
                : exclude;
    }

    /**
     * Exclude listed OpenAPI files (if {@link #include} is not specified)
     */
    void setExclude(List<String> exclude) {
        this.exclude = new HashSet<>(exclude);
    }

    public @NotNull Set<String> getInclude() {
        return CollectionUtils.isEmpty(include)
                ? Collections.emptySet()
                : include;
    }

    /**
     * Include only listed OpenAPI files (ignores {@link #exclude})
     */
    void setInclude(List<String> include) {
        this.include = new HashSet<>(include);
    }
}
