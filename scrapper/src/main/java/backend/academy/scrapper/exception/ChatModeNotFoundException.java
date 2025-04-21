package backend.academy.scrapper.exception;

import org.springframework.http.HttpStatus;

public class ChatModeNotFoundException extends ScrapperException {
    public ChatModeNotFoundException() {
        super("Мод c данный чатом отсутствует", "мод в чате отсутствует", HttpStatus.BAD_REQUEST);
    }
}
