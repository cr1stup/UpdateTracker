package backend.academy.bot.repository;

import backend.academy.bot.handler.state.BotState;
import java.util.HashMap;
import java.util.Map;

public final class BotStateRepository {
    private static final Map<Long, BotState> states = new HashMap<>();

    public static void setState(Long id, BotState state) {
        states.put(id, state);
    }

    public static BotState getState(Long id) {
        return states.getOrDefault(id, BotState.START);
    }
}
