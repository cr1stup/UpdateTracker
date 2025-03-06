package backend.academy.scrapper.dto;

import backend.academy.scrapper.util.OptionalAnswerDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = OptionalAnswerDeserializer.class)
public record OptionalAnswer<T>(T answer, ApiErrorResponse apiErrorResponse) {

    public static <T> OptionalAnswer<T> error(ApiErrorResponse apiErrorResponse) {
        return new OptionalAnswer<>(null, apiErrorResponse);
    }

    public static <T> OptionalAnswer<T> of(T value) {
        return new OptionalAnswer<>(value, null);
    }
}
