package backend.academy.scrapper.exception;

import org.springframework.http.HttpStatus;

public class UnknownModeException extends ScrapperException {
    public UnknownModeException() {
        super("Мод отсутствует", "данный мод отсутствует", HttpStatus.BAD_REQUEST);
    }
}
