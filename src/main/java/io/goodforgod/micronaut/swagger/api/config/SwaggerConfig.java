package io.goodforgod.micronaut.swagger.api.config;


import io.goodforgod.micronaut.swagger.api.SwaggerSettings;
import io.goodforgod.micronaut.swagger.api.controller.SwaggerController;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.core.util.CollectionUtils;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;


/**
 * Configuration for swagger library
 *
 * @author Anton Kurako (GoodforGod)
 * @since 20.9.2020
 */
@ConfigurationProperties(SwaggerSettings.PREFIX)
public class SwaggerConfig {

    /**
     * Enables {@link SwaggerController} controller
     */
    private boolean enabled = true;

    /**
     * Allow merging multiple swaggers into one
     */
    private boolean merge = false;

    /**
     * Allow change {@link SwaggerController} controller path
     */
    private String path = SwaggerSettings.DEFAULT_SWAGGER_URL;

    /**
     * Exclude listed swagger files (if {@link #include} is not specified)
     */
    private Set<String> exclude;

    /**
     * Include only listed swagger files (ignores {@link #exclude})
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
