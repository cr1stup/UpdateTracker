package backend.academy.bot.repository;

import backend.academy.bot.handler.state.BotState;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Repository;

@Repository
public class DefaultBotRepository implements BotRepository  {
    private static final Map<Long, Params> botParams = new HashMap<>();

    @Override
    public void setState(Long id, BotState state) {
        if (state != BotState.TAG && state != BotState.FILTER) {
            botParams.put(id, new Params().botState(state));
        } else {
            botParams.get(id).botState(state);
        }
    }

    @Override
    public BotState getState(Long id) {
        return botParams.containsKey(id)
            ? botParams.get(id).botState
            : BotState.START;
    }

    @Override
    public void setLink(Long id, String link) {
        botParams.get(id).link(link);
    }

    @Override
    public String getLink(Long id) {
        return botParams.containsKey(id)
            ? botParams.get(id).link
            : null;
    }

    @Override
    public void setTags(Long id, List<String> tags) {
        botParams.get(id).tags(tags);
    }

    @Override
    public List<String> getTags(Long id) {
        return botParams.containsKey(id)
            ? botParams.get(id).tags
            : null;
    }

    @Override
    public void setFilters(Long id, List<String> filters) {
        botParams.get(id).filters(filters);
    }

    @Override
    public List<String> getFilters(Long id) {
        return botParams.containsKey(id)
            ? botParams.get(id).filters
            : null;
    }

    @Getter
    @Setter
    private static class Params {
        private BotState botState;
        private String link;
        private List<String> tags;
        private List<String> filters;
    }
}
