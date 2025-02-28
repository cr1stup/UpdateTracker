package backend.academy.bot.handler.command;

import backend.academy.bot.handler.state.BotState;
import backend.academy.bot.repository.BotStateRepository;
import backend.academy.bot.util.BotMessages;
import com.pengrad.telegrambot.model.Update;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(4)
public class UntrackCommand implements Command {
    @Override
    public String name() {
        return BotMessages.UNTRACK_NAME;
    }

    @Override
    public String description() {
        return BotMessages.UNTRACK_DESCRIPTION;
    }

    @Override
    public void setState(Update update) {
        BotStateRepository.setState(update.message().chat().id() ,BotState.UNTRACK);
    }
}
