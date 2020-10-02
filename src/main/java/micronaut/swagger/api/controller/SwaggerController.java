package micronaut.swagger.api.controller;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.server.types.files.FileCustomizableResponseType;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.http.server.types.files.SystemFile;
import io.reactivex.Maybe;
import io.swagger.v3.oas.annotations.Hidden;
import micronaut.swagger.api.SwaggerSettings;
import micronaut.swagger.api.service.SwaggerLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.InputStream;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 20.9.2020
 */
@Hidden
@Requires(property = SwaggerSettings.PREFIX + ".enabled", value = "true", defaultValue = "true")
@Controller("${swagger.path:" + SwaggerSettings.DEFAULT_SWAGGER_URL + "}")
public class SwaggerController {

    private static final String YAML_CONTENT_TYPE = "text/x-yaml;charset=utf-8";

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final SwaggerLoader loader;
    private final MediaType mediaType;

    @Inject
    public SwaggerController(SwaggerLoader loader) {
        this.loader = loader;
        this.mediaType = MediaType.of(YAML_CONTENT_TYPE);
    }

    @Get(produces = YAML_CONTENT_TYPE)
    public Maybe<FileCustomizableResponseType> getSwagger() {
        return loader.getSwagger().map(s -> {
            final InputStream stream = s.getInputStream();
            if (stream != null) {
                logger.debug("Streaming swagger in path: {}", s.getUri().getPath());
                return new StreamedFile(stream, mediaType);
            }

            logger.debug("System swagger in path: {}", s.getUri().getPath());
            return new SystemFile(new File(s.getUri().getPath()), mediaType);
        });
    }
}
