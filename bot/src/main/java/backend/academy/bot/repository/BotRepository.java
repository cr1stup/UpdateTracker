package backend.academy.bot.repository;

import backend.academy.bot.handler.state.model.BotState;
import java.util.List;

public interface BotRepository {
    void setState(Long id, BotState state);

    BotState getState(Long id);

    void setLink(Long id, String link);

    String getLink(Long id);

    void setTags(Long id, List<String> tags);

    List<String> getTags(Long id);

    void setFilters(Long id, List<String> filters);

    List<String> getFilters(Long id);
}
