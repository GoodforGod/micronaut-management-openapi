package micronaut.swagger.api.data;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.reactivex.Single;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 25.9.2020
 */
@Requires(property = "stub.first.enabled", value = "true", defaultValue = "false")
@Controller("/first")
public class FirstController {

    @Get(produces = MediaType.TEXT_PLAIN)
    public Single<String> getSurname() {
        return Single.just("Tower");
    }
}
