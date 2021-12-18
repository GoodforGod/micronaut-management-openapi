package io.goodforgod.micronaut.swagger.api;


import io.micronaut.context.annotation.Requires;
import java.lang.annotation.*;


/**
 * @author Anton Kurako (GoodforGod)
 * @since 24.9.2020
 */
@Requires(property = SwaggerSettings.PREFIX)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PACKAGE, ElementType.TYPE })
@interface RequiresSwagger {
}
