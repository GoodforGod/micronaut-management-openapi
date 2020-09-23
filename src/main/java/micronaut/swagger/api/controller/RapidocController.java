package micronaut.swagger.api.controller;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.server.types.files.StreamedFile;
import io.reactivex.Maybe;
import io.swagger.v3.oas.annotations.Hidden;
import micronaut.swagger.api.SwaggerSettings;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 22.9.2020
 */
@Hidden
@Requires(property = SwaggerSettings.PREFIX + ".rapidoc.enabled", value = "true", defaultValue = "false")
@Controller("${swagger.rapidoc.path:" + SwaggerSettings.DEFAULT_RAPIDOC_URL + "}")
public class RapidocController extends FileController {

    @Get(produces = MediaType.TEXT_HTML)
    public Maybe<StreamedFile> getSwagger() {
        return getFile("rapidoc/index.html", MediaType.TEXT_HTML_TYPE);
    }
}
