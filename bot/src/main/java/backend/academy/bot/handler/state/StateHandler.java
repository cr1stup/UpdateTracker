package backend.academy.bot.handler.state;

import backend.academy.bot.handler.state.model.BotState;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

public interface StateHandler {
    BotState state();

    SendMessage handle(Update update);

    default boolean isSupport(BotState state) {
        return state.equals(state());
    }
}
