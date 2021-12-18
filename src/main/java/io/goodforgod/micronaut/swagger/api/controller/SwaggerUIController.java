package io.goodforgod.micronaut.swagger.api.controller;


import io.goodforgod.micronaut.swagger.api.SwaggerSettings;
import io.goodforgod.micronaut.swagger.api.config.SwaggerConfig;
import io.goodforgod.micronaut.swagger.api.config.SwaggerUIConfig;
import io.goodforgod.micronaut.swagger.api.utils.ResourceUtils;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.env.DefaultPropertyPlaceholderResolver;
import io.micronaut.core.convert.ConversionService;
import io.micronaut.core.util.StringUtils;
import io.micronaut.core.util.SupplierUtil;
import io.micronaut.core.value.MapPropertyResolver;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.server.types.files.StreamedFile;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.function.Supplier;


/**
 * @author Anton Kurako (GoodforGod)
 * @since 20.9.2020
 */
@Hidden
@Requires(classes = StreamedFile.class)
@Requires(property = SwaggerSettings.PREFIX + ".swagger-ui.enabled", value = StringUtils.TRUE, defaultValue = StringUtils.TRUE)
@Controller("${swagger.swagger-ui.path:" + SwaggerSettings.DEFAULT_SWAGGER_UI_URL + "}")
public class SwaggerUIController {

    private static final String SWAGGER_IU_PATH = "swagger-api/swagger-ui/index.html";
    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";

    private final Supplier<String> resolvedFile;

    @Inject
    public SwaggerUIController(SwaggerConfig swaggerConfig,
                               SwaggerUIConfig swaggerUIConfig,
                               ConversionService conversionService,
                               @Value("${micronaut.application.name:Swagger UI}") String serviceName) {
        this.resolvedFile = SupplierUtil.memoized(() -> {
            final String file = ResourceUtils.getFileAsString(SWAGGER_IU_PATH)
                    .orElseThrow(() -> new IllegalArgumentException("Cannot find Swagger-UI: " + SWAGGER_IU_PATH));

            final Map<String, Object> map = Map.of(
                    "serviceName", serviceName,
                    "swaggerPath", swaggerConfig.getPath(),
                    "swaggerUIPath", swaggerUIConfig.getPath());

            var propertyResolver = new MapPropertyResolver(map);
            var placeholderResolver = new DefaultPropertyPlaceholderResolver(propertyResolver, conversionService);
            return placeholderResolver.resolvePlaceholders(file).get();
        });
    }

    @Get(produces = HTML_CONTENT_TYPE)
    public String getSwagger() {
        return resolvedFile.get();
    }
}
