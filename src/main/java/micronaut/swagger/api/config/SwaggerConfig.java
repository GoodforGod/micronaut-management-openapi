package micronaut.swagger.api.config;

import io.micronaut.context.annotation.ConfigurationBuilder;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.CollectionUtils;
import micronaut.swagger.api.SwaggerSettings;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * Configuration for swagger library
 *
 * @author Anton Kurako (GoodforGod)
 * @since 20.9.2020
 */
@Requires(property = SwaggerSettings.PREFIX)
@ConfigurationProperties(SwaggerSettings.PREFIX)
public class SwaggerConfig {

    public class SwaggerUIConfig {

        private String path = SwaggerSettings.DEFAULT_SWAGGER_UI_URL;
        private boolean enabled = true;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public class RapidocConfig {

        private String path = SwaggerSettings.DEFAULT_RAPIDOC_URL;
        private boolean enabled = false;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    private boolean enabled = true;
    private boolean merge = true;
    private String path = SwaggerSettings.DEFAULT_SWAGGER_URL;
    private List<String> exclude;

    @ConfigurationBuilder(prefixes = "set", configurationPrefix = "ui")
    private final SwaggerUIConfig uiConfig = new SwaggerUIConfig();

    @ConfigurationBuilder(prefixes = "set", configurationPrefix = "rapidoc")
    private final RapidocConfig rapidocConfig = new RapidocConfig();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isMerge() {
        return merge;
    }

    public void setMerge(boolean merge) {
        this.merge = merge;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public @NotNull List<String> getExclude() {
        return CollectionUtils.isEmpty(exclude) ? Collections.emptyList() : exclude;
    }

    public void setExclude(List<String> exclude) {
        this.exclude = exclude;
    }

    public SwaggerUIConfig getUiConfig() {
        return uiConfig;
    }

    public RapidocConfig getRapidocConfig() {
        return rapidocConfig;
    }
}
