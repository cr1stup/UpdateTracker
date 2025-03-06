package backend.academy.scrapper.exception;

import org.springframework.http.HttpStatus;

public class ChatAlreadyRegisteredException extends ScrapperException {
    public ChatAlreadyRegisteredException() {
        super("Повторная регистрация чата", "Вы уже зарегистрированы", HttpStatus.BAD_REQUEST);
    }
}
