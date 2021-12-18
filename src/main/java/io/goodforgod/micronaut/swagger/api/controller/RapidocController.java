package io.goodforgod.micronaut.swagger.api.controller;


import io.goodforgod.micronaut.swagger.api.SwaggerSettings;
import io.goodforgod.micronaut.swagger.api.config.RapidocConfig;
import io.goodforgod.micronaut.swagger.api.config.SwaggerConfig;
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
 * @since 22.9.2020
 */
@Hidden
@Requires(classes = StreamedFile.class)
@Requires(property = SwaggerSettings.PREFIX + ".rapidoc.enabled", value = StringUtils.TRUE, defaultValue = StringUtils.FALSE)
@Controller("${swagger.rapidoc.path:" + SwaggerSettings.DEFAULT_RAPIDOC_URL + "}")
public class RapidocController {

    private static final String RAPIDOC_PATH = "swagger-api/rapidoc/index.html";
    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";

    private final Supplier<String> resolvedFile;

    @Inject
    public RapidocController(SwaggerConfig swaggerConfig,
                             RapidocConfig rapidocConfig,
                             ConversionService conversionService,
                             @Value("${micronaut.application.name:Swagger UI}") String serviceName) {
        this.resolvedFile = SupplierUtil.memoized(() -> {
            final String file = ResourceUtils.getFileAsString(RAPIDOC_PATH)
                    .orElseThrow(() -> new IllegalArgumentException("Cannot find Rapidoc: " + RAPIDOC_PATH));

            final Map<String, Object> map = Map.of(
                    "serviceName", serviceName,
                    "swaggerPath", swaggerConfig.getPath(),
                    "rapidocPath", rapidocConfig.getPath());

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
