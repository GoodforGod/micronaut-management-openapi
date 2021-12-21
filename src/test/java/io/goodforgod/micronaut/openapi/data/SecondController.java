package io.goodforgod.micronaut.openapi.data;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 25.9.2020
 */
@Requires(property = "stub.second.enabled", value = "true", defaultValue = "false")
@Controller("/second")
public class SecondController {

    @Get(produces = MediaType.TEXT_PLAIN)
    public String getName() {
        return "Bob";
    }
}
