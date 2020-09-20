package micronaut.swagger.api.config;

import io.micronaut.context.annotation.ConfigurationProperties;
import io.micronaut.context.annotation.Requires;
import micronaut.swagger.api.SwaggerSettings;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 20.9.2020
 */
@Requires(property = SwaggerSettings.PREFIX)
@ConfigurationProperties(SwaggerSettings.PREFIX)
public class SwaggerConfig {

    public class SwaggerUIConfig {
        private boolean enabled;
    }

    private boolean enabled;
}
