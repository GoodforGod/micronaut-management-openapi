package micronaut.swagger.api.config;

import io.micronaut.context.annotation.Requires;
import micronaut.swagger.api.SwaggerSettings;

import java.lang.annotation.*;

/**
 * A custom requirement for ArangoDB.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 24.9.2020
 */
@Requires(property = SwaggerSettings.PREFIX)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PACKAGE, ElementType.TYPE })
public @interface RequiresSwagger {
}
