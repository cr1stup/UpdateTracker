package backend.academy.scrapper.exception;

import org.springframework.http.HttpStatus;
import java.net.URI;

public class LinkNotFoundException extends ScrapperException {
    public LinkNotFoundException(URI link) {
        super(
            "Ссылка отсутствует",
            "ссылка %s не добавлена для отслеживания".formatted(link.toString()),
            HttpStatus.BAD_REQUEST
        );
    }
}
