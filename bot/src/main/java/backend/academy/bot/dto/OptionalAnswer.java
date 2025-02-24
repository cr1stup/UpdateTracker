package backend.academy.bot.dto;

import java.util.function.Consumer;

public record OptionalAnswer<T>(T answer, ApiErrorResponse apiErrorResponse) {

    public static <T> OptionalAnswer<T> error(ApiErrorResponse apiErrorResponse) {
        return new OptionalAnswer<>(null, apiErrorResponse);
    }

    public static <T> OptionalAnswer<T> of(T value) {
        return new OptionalAnswer<>(value, null);
    }

    public boolean isError() {
        return apiErrorResponse != null;
    }

    public OptionalAnswer<T> ifError(Consumer<ApiErrorResponse> consumer) {
        if (isError()) {
            consumer.accept(apiErrorResponse);
        }
        return this;
    }
}
