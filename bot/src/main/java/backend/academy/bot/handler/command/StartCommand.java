package backend.academy.bot.handler.command;

import backend.academy.bot.handler.state.BotState;
import backend.academy.bot.repository.BotRepository;
import backend.academy.bot.util.BotMessages;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@RequiredArgsConstructor
public class StartCommand implements Command {

    private final BotRepository botRepository;

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
        botRepository.setState(update.message().chat().id(), BotState.START);
    }
}
