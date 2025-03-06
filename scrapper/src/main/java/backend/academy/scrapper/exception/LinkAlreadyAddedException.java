package backend.academy.scrapper.exception;

import org.springframework.http.HttpStatus;

import java.net.URI;

public class LinkAlreadyAddedException extends ScrapperException {
    public LinkAlreadyAddedException(URI link) {
        super(
            "Повторное добавление ссылки",
            "ссылка %s уже добавлена".formatted(link.toString()),
            HttpStatus.BAD_REQUEST
        );
    }
}
