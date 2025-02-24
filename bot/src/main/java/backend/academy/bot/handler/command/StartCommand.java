package backend.academy.bot.handler.command;

import backend.academy.bot.handler.state.BotState;
import backend.academy.bot.repository.BotStateRepository;
import backend.academy.bot.util.BotMessages;
import com.pengrad.telegrambot.model.Update;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class StartCommand implements Command {

    @Override
    public String name() {
        return BotMessages.START_NAME;
    }

    @Override
    public String description() {
        return BotMessages.START_DESCRIPTION;
    }

    @Override
    public void setState(Update update) {
        BotStateRepository.setState(update.message().chat().id(), BotState.START);
    }
}
