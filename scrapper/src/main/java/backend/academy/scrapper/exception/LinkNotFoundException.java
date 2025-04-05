package backend.academy.scrapper.exception;

import org.springframework.http.HttpStatus;

public class LinkNotFoundException extends ScrapperException {
    public LinkNotFoundException() {
        super("Ссылка отсутствует", "данная ссылка не добавлена для отслеживания", HttpStatus.BAD_REQUEST);
    }
}
