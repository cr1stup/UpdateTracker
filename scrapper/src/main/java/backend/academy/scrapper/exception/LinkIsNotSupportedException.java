package backend.academy.scrapper.exception;

import org.springframework.http.HttpStatus;

import java.net.URI;

public class LinkIsNotSupportedException extends ScrapperException {

    public LinkIsNotSupportedException(URI link) {
        super(
            "Ссылка не поддерживается",
            "ссылка %s не поддерживается".formatted(link.toString()),
            HttpStatus.BAD_REQUEST
        );
    }
}
