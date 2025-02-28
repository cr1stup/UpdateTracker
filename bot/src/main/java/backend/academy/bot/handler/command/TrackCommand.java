package backend.academy.bot.handler.command;

import backend.academy.bot.handler.state.BotState;
import backend.academy.bot.repository.BotStateRepository;
import backend.academy.bot.util.BotMessages;
import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Component;

@Component
public class TrackCommand implements Command {
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
        BotStateRepository.setState(update.message().chat().id(), BotState.TRACK);
    }
}
