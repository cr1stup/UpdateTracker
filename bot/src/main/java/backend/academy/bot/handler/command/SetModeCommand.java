package backend.academy.bot.handler.command;

import backend.academy.bot.handler.state.model.BotState;
import backend.academy.bot.repository.BotRepository;
import backend.academy.bot.util.BotMessages;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(7)
@RequiredArgsConstructor
public class SetModeCommand implements Command {

    private final BotRepository botRepository;

    @Override
    public String name() {
        return BotMessages.SETMODE_NAME;
    }

    @Override
    public String description() {
        return BotMessages.SETMODE_DESCRIPTION;
    }

    @Override
    public void setState(Update update) {
        botRepository.setState(update.message().chat().id(), BotState.CHECK_MODE);
    }
}
