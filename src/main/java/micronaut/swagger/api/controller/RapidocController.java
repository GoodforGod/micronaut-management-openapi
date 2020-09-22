package micronaut.swagger.api.controller;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.reactivex.Maybe;
import io.swagger.v3.oas.annotations.Hidden;
import micronaut.swagger.api.SwaggerSettings;

/**
 * Description in progress
 *
 * @author Anton Kurako (GoodforGod)
 * @since 22.9.2020
 */
@Hidden
@Requires(property = SwaggerSettings.PREFIX + ".rapidoc.enabled", value = "true", defaultValue = "false")
@Controller("${swagger.rapidoc.path:/rapidoc}")
public class RapidocController {

    @Get(produces = MediaType.TEXT_HTML)
    public Maybe<String> getSwagger() {
        return Maybe.empty();
    }
}
