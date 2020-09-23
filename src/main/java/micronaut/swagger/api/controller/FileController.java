package micronaut.swagger.api.controller;

import io.micronaut.http.MediaType;
import io.micronaut.http.server.types.files.StreamedFile;
import io.reactivex.Maybe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Description in progress
 *
 * @author Anton Kurako (GoodforGod)
 * @since 23.9.2020
 */
public abstract class FileController {

protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected Maybe<StreamedFile> getFile(String path, MediaType type) {
        return Maybe.fromCallable(() -> Optional.ofNullable(getClass().getResourceAsStream(path)))
                .filter(Optional::isPresent)
                .switchIfEmpty(Maybe.fromCallable(() -> Optional.ofNullable(getClass().getResourceAsStream("/" + path))))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(s -> {
                    logger.debug("Streaming file in path: {}", path);
                    return new StreamedFile(s, type);
                });
    }
}
