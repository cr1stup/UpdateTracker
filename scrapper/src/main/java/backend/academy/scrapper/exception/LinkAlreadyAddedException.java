package backend.academy.scrapper.exception;

import java.net.URI;
import org.springframework.http.HttpStatus;

public class LinkAlreadyAddedException extends ScrapperException {
    public LinkAlreadyAddedException(URI link) {
        super(
                "Повторное добавление ссылки",
                "ссылка %s уже добавлена".formatted(link.toString()),
                HttpStatus.BAD_REQUEST);
    }
}
