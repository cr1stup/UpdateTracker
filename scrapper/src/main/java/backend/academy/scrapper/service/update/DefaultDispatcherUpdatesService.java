package backend.academy.scrapper.service.update;

import backend.academy.scrapper.dto.LinkUpdate;
import backend.academy.scrapper.repository.redis.DefaultBatchedLinkUpdateRepository;
import backend.academy.scrapper.service.chat.ChatModeService;
import backend.academy.scrapper.service.model.Mode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultDispatcherUpdatesService implements DispatcherUpdatesService {

    private final ImmediateUpdateService immediateUpdateService;
    private final ChatModeService chatModeService;
    private final DefaultBatchedLinkUpdateRepository batchedRepository;

    @Override
    public void dispatchUpdates(LinkUpdate linkUpdate) {
        List<Long> chatIdsImmediate = chatModeService.findAllChatIdsByMode(Mode.IMMEDIATE.modeName());
        List<Long> chatIdsDaily = chatModeService.findAllChatIdsByMode(Mode.DAILY.modeName());

        immediateUpdateService.sendUpdatesToUsers(linkUpdate.withChatIds(chatIdsImmediate));

        for (Long chatId : chatIdsDaily) {
            batchedRepository.addToBatch(chatId, linkUpdate.withChatIds(List.of(chatId)));
        }
    }
}
