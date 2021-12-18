package io.goodforgod.micronaut.openapi;


import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.client.annotation.Client;


/**
 * @author Anton Kurako (GoodforGod)
 * @since 25.9.2020
 */
@Client("/")
public interface SwaggerClient {

    @Get(uri = OpenAPISettings.DEFAULT_OPENAPI_URL, consumes = "text/x-yaml")
    HttpResponse<?> getSwagger();

    @Get(uri = OpenAPISettings.DEFAULT_SWAGGER_UI_URL, consumes = MediaType.TEXT_HTML)
    HttpResponse<?> getSwaggerUI();

    @Get(uri = OpenAPISettings.DEFAULT_RAPIDOC_URL, consumes = MediaType.TEXT_HTML)
    HttpResponse<?> getRapidoc();
}
