package io.goodforgod.micronaut.openapi.controller;


import io.goodforgod.micronaut.openapi.OpenAPISettings;
import io.goodforgod.micronaut.openapi.config.OpenAPIConfig;
import io.goodforgod.micronaut.openapi.config.SwaggerUIConfig;
import io.goodforgod.micronaut.openapi.utils.ResourceUtils;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.env.DefaultPropertyPlaceholderResolver;
import io.micronaut.core.convert.ConversionService;
import io.micronaut.core.util.StringUtils;
import io.micronaut.core.util.SupplierUtil;
import io.micronaut.core.value.MapPropertyResolver;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.exceptions.HttpStatusException;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.function.Supplier;


/**
 * @author Anton Kurako (GoodforGod)
 * @since 20.9.2020
 */
@Requires(property = OpenAPISettings.PREFIX + ".swagger-ui.enabled", value = StringUtils.TRUE, defaultValue = StringUtils.TRUE)
@Controller("${swagger.swagger-ui.path:" + OpenAPISettings.DEFAULT_SWAGGER_UI_URL + "}")
public class SwaggerUIController {

    private static final String SWAGGER_IU_PATH = "micronaut-management-openapi/swagger-ui/index.html";
    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";

    private final Supplier<String> resolvedValue;

    @Inject
    public SwaggerUIController(OpenAPIConfig openAPIConfig,
                               SwaggerUIConfig swaggerUIConfig,
                               ConversionService conversionService,
                               @Value("${micronaut.application.name:Swagger UI}") String serviceName) {
        this.resolvedValue = SupplierUtil.memoized(() -> {
            final String file = ResourceUtils.getFileAsString(SWAGGER_IU_PATH).orElse(StringUtils.EMPTY_STRING);

            final Map<String, Object> map = Map.of(
                    "serviceName", serviceName,
                    "swaggerPath", openAPIConfig.getPath(),
                    "swaggerUIPath", swaggerUIConfig.getPath());

            var propertyResolver = new MapPropertyResolver(map);
            var placeholderResolver = new DefaultPropertyPlaceholderResolver(propertyResolver, conversionService);
            return placeholderResolver.resolvePlaceholders(file).get();
        });
    }

    @Get(produces = HTML_CONTENT_TYPE)
    @ExecuteOn(TaskExecutors.IO)
    public String get() {
        final String value = resolvedValue.get();
        if (StringUtils.isEmpty(value)) {
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "Can't find Swagger UI file: " + SWAGGER_IU_PATH);
        } else {
            return value;
        }
    }
}
