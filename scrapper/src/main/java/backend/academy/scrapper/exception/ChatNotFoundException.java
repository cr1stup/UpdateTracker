package backend.academy.scrapper.exception;

import org.springframework.http.HttpStatus;

public class ChatNotFoundException extends ScrapperException {
    public ChatNotFoundException() {
        super("Отсутствует чат", "вы не зарегистрированы", HttpStatus.NOT_FOUND);
    }
}
