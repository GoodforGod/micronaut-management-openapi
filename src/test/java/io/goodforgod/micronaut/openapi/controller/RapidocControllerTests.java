package io.goodforgod.micronaut.openapi.controller;


import io.goodforgod.micronaut.openapi.SwaggerClient;
import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * @author Anton Kurako (GoodforGod)
 * @since 25.9.2020
 */
@Property(name = "openapi.rapidoc.enabled", value = "true")
@MicronautTest
class RapidocControllerTests extends Assertions {

    @Inject
    private SwaggerClient client;

    @Test
    void isPresent() {
        final HttpResponse<?> response = client.getRapidoc();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.status());
        assertNotNull(response.body());
    }
}
