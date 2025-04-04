package backend.academy.scrapper.client.link.stackoverflow.dto;

import java.util.List;

public record QuestionResponse(List<QuestionItem> items) {
    public static final QuestionResponse EMPTY = new QuestionResponse(null);
}
