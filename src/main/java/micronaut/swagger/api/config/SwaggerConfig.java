package micronaut.swagger.api.config;

import io.micronaut.context.annotation.ConfigurationBuilder;
import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;
import micronaut.swagger.api.SwaggerSettings;

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

        private String path;
        private boolean enabled;

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

    private boolean enabled;
    private boolean merge;
    private String path;
    private List<String> exclude;

    @ConfigurationBuilder(prefixes = "set", configurationPrefix = "ui")
    private final SwaggerUIConfig uiConfig = new SwaggerUIConfig();

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

    public List<String> getExclude() {
        return exclude;
    }

    public void setExclude(List<String> exclude) {
        this.exclude = exclude;
    }

    public SwaggerUIConfig getUiConfig() {
        return uiConfig;
    }

    @Override
    public String toString() {
        return "SwaggerConfig{" +
                "enabled=" + enabled +
                ", merge=" + merge +
                ", path='" + path + '\'' +
                ", exclude=" + exclude +
                ", uiConfig=" + uiConfig +
                '}';
    }
}
