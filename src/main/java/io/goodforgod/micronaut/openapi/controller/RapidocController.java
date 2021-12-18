package io.goodforgod.micronaut.openapi.controller;


import io.goodforgod.micronaut.openapi.OpenAPISettings;
import io.goodforgod.micronaut.openapi.config.OpenAPIConfig;
import io.goodforgod.micronaut.openapi.config.RapidocConfig;
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
 * @since 22.9.2020
 */
@Requires(property = OpenAPISettings.PREFIX + ".rapidoc.enabled", value = StringUtils.TRUE, defaultValue = StringUtils.FALSE)
@Controller("${" + OpenAPISettings.PREFIX + ".rapidoc.path:" + OpenAPISettings.DEFAULT_RAPIDOC_URL + "}")
public class RapidocController {

    private static final String RAPIDOC_PATH = "micronaut-management-openapi/rapidoc/index.html";
    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";

    private final Supplier<String> resolvedValue;

    @Inject
    public RapidocController(OpenAPIConfig openAPIConfig,
                             RapidocConfig rapidocConfig,
                             ConversionService conversionService,
                             @Value("${micronaut.application.name:Swagger UI}") String serviceName) {
        this.resolvedValue = SupplierUtil.memoized(() -> {
            final String file = ResourceUtils.getFileAsString(RAPIDOC_PATH).orElse(StringUtils.EMPTY_STRING);

            final Map<String, Object> map = Map.of(
                    "serviceName", serviceName,
                    "swaggerPath", openAPIConfig.getPath(),
                    "rapidocPath", rapidocConfig.getPath());

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
            throw new HttpStatusException(HttpStatus.NOT_FOUND, "Can't find Rapidoc file: " + RAPIDOC_PATH);
        } else {
            return value;
        }
    }
}
