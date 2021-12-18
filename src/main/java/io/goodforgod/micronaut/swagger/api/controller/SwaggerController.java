package io.goodforgod.micronaut.swagger.api.controller;


import io.goodforgod.micronaut.swagger.api.SwaggerSettings;
import io.goodforgod.micronaut.swagger.api.model.URIResource;
import io.goodforgod.micronaut.swagger.api.service.SwaggerLoader;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.server.types.files.FileCustomizableResponseType;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.http.server.types.files.SystemFile;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.inject.Inject;
import java.io.File;
import java.io.InputStream;
import reactor.core.publisher.Mono;


/**
 * @author Anton Kurako (GoodforGod)
 * @since 20.9.2020
 */
@Hidden
@Requires(classes = StreamedFile.class)
@Requires(property = SwaggerSettings.PREFIX + ".enabled", value = StringUtils.TRUE, defaultValue = StringUtils.TRUE)
@Controller("${swagger.path:" + SwaggerSettings.DEFAULT_SWAGGER_URL + "}")
public class SwaggerController {

    private static final String YAML_CONTENT_TYPE = "text/x-yaml;charset=utf-8";

    private final SwaggerLoader loader;
    private final MediaType mediaType;

    @Inject
    public SwaggerController(SwaggerLoader loader) {
        this.loader = loader;
        this.mediaType = MediaType.of(YAML_CONTENT_TYPE);
    }

    @Get(produces = YAML_CONTENT_TYPE)
    public Mono<FileCustomizableResponseType> getSwagger() {
        return loader.getSwagger()
                .map(resource -> {
                    final InputStream stream = resource.getInputStream();
                    if (stream != null) {
                        return new StreamedFile(stream, mediaType);
                    } else if (resource instanceof URIResource) {
                        return new SystemFile(new File(((URIResource) resource).getUri().getPath()), mediaType);
                    } else {
                        throw new IllegalStateException("Unknown resource without InputStream and URI");
                    }
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("No Swagger File Found")));
    }
}
