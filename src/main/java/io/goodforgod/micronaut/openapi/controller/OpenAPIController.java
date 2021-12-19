package io.goodforgod.micronaut.openapi.controller;


import io.goodforgod.micronaut.openapi.OpenAPISettings;
import io.goodforgod.micronaut.openapi.config.OpenAPIConfig;
import io.goodforgod.micronaut.openapi.model.FileResource;
import io.goodforgod.micronaut.openapi.model.PathResource;
import io.goodforgod.micronaut.openapi.model.Resource;
import io.goodforgod.micronaut.openapi.service.OpenAPIProvider;
import io.micronaut.context.annotation.Requires;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.http.server.types.files.FileCustomizableResponseType;
import io.micronaut.http.server.types.files.StreamedFile;
import io.micronaut.http.server.types.files.SystemFile;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.inject.Inject;
import java.io.File;
import java.io.InputStream;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Anton Kurako (GoodforGod)
 * @since 20.9.2020
 */
@Hidden
@Requires(classes = FileCustomizableResponseType.class)
@Requires(property = OpenAPISettings.PREFIX + ".enabled", value = StringUtils.TRUE, defaultValue = StringUtils.TRUE)
@Controller("${" + OpenAPISettings.PREFIX + ".path:" + OpenAPISettings.DEFAULT_OPENAPI_URL + "}")
public class OpenAPIController {

    private static final Logger logger = LoggerFactory.getLogger(OpenAPIController.class);

    private static final String YAML_CONTENT_TYPE = "text/x-yaml;charset=utf-8";
    private static final MediaType MEDIA_TYPE = MediaType.of(YAML_CONTENT_TYPE);

    private final OpenAPIProvider openAPIProvider;
    private final OpenAPIConfig openAPIConfig;

    @Inject
    public OpenAPIController(OpenAPIProvider openAPIProvider,
                             OpenAPIConfig openAPIConfig) {
        this.openAPIProvider = openAPIProvider;
        this.openAPIConfig = openAPIConfig;
    }

    @Get(produces = YAML_CONTENT_TYPE)
    @ExecuteOn(TaskExecutors.IO)
    public FileCustomizableResponseType get() {
        final Optional<Resource> openapi = openAPIConfig.isMerge()
                ? openAPIProvider.getMerged()
                : openAPIProvider.getAny();

        if (openapi.isEmpty()) {
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "Can't find OpenAPI file");
        } else if (openapi.get() instanceof FileResource) {
            final File file = ((FileResource) openapi.get()).getFile();
            logger.debug("Streaming file with path: {}", file);
            return new SystemFile(file, MEDIA_TYPE);
        } else {
            final InputStream inputStream = openapi.get().getStream();
            if (inputStream != null) {
                return new StreamedFile(inputStream, MEDIA_TYPE);
            } else if (openapi.get() instanceof PathResource) {
                throw new HttpStatusException(HttpStatus.NO_RESPONSE,
                        "Can't read OpenAPI file: " + ((PathResource) openapi.get()).getPath());
            } else {
                throw new HttpStatusException(HttpStatus.NO_RESPONSE, "Can't read OpenAPI file cause it was empty");
            }
        }
    }
}
