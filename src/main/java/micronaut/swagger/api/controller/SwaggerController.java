package micronaut.swagger.api.controller;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.server.types.files.StreamedFile;
import io.reactivex.Maybe;
import io.reactivex.Single;
import micronaut.swagger.api.SwaggerSettings;
import micronaut.swagger.api.config.SwaggerConfig;
import micronaut.swagger.api.service.SwaggerLoader;

import javax.inject.Inject;
import java.io.InputStream;
import java.util.Optional;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 20.9.2020
 */
@Requires(property = SwaggerSettings.PREFIX + ".enabled", value = "true", defaultValue = "true")
@Controller("${swagger.path:/swagger}")
public class SwaggerController {

    private final SwaggerLoader loader;

    @Inject
    public SwaggerController(SwaggerLoader loader) {
        this.loader = loader;
    }

    @Get(produces = MediaType.APPLICATION_YAML)
    public Maybe<StreamedFile> getSwagger() {
        return loader.getSwagger().map(s -> {
            final InputStream stream = getClass().getResourceAsStream(s.getUri().getPath());
            return new StreamedFile(stream, MediaType.APPLICATION_YAML_TYPE);
        });
    }
}
