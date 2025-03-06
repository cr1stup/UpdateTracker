package backend.academy.scrapper.exception;

import java.net.URI;
import org.springframework.http.HttpStatus;

public class LinkNotFoundException extends ScrapperException {
    public LinkNotFoundException(URI link) {
        super(
                "Ссылка отсутствует",
                "ссылка %s не добавлена для отслеживания".formatted(link.toString()),
                HttpStatus.BAD_REQUEST);
    }
}
