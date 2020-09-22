package micronaut.swagger.api.controller;

import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.server.types.files.StreamedFile;
import io.reactivex.Maybe;
import micronaut.swagger.api.SwaggerSettings;
import micronaut.swagger.api.service.SwaggerLoader;

import javax.inject.Inject;
import java.io.InputStream;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 20.9.2020
 */
@Requires(property = SwaggerSettings.PREFIX + ".ui.enabled", value = "true", defaultValue = "true")
@Controller("${swagger.ui.path:/swagger/ui}")
public class SwaggerUIController {

    @Value("${swagger.ui.path:/swagger/ui}")
    private String swaggerPath;

    @Get(produces = MediaType.APPLICATION_YAML)
    public Maybe<String> getSwagger() {
        return Maybe.empty();
    }
}
