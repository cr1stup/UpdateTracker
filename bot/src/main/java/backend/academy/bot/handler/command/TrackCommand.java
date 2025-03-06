package backend.academy.bot.handler.command;

import backend.academy.bot.handler.state.BotState;
import backend.academy.bot.repository.BotRepository;
import backend.academy.bot.util.BotMessages;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(3)
@RequiredArgsConstructor
public class TrackCommand implements Command {

    private final BotRepository botRepository;

    @Override
    public String name() {
        return BotMessages.TRACK_NAME;
    }

    @Override
    public String description() {
        return BotMessages.TRACK_DESCRIPTION;
    }

    @Override
    public void setState(Update update) {
        botRepository.setState(update.message().chat().id(), BotState.TRACK);
    }
}
