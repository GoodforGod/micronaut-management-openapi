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
 * @since 20.9.2020
 */
@Hidden
@Requires(property = SwaggerSettings.PREFIX + ".ui.enabled", value = "true", defaultValue = "true")
@Controller("${swagger.ui.path:" + SwaggerSettings.DEFAULT_SWAGGER_UI_URL + "}")
public class SwaggerUIController extends FileController {

    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";

    @Get(produces = HTML_CONTENT_TYPE)
    public Maybe<StreamedFile> getSwagger() {
        return getFile("swagger-api/swagger-ui/index.html", MediaType.TEXT_HTML_TYPE);
    }
}
