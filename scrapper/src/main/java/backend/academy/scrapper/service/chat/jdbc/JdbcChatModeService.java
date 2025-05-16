package backend.academy.scrapper.service.chat.jdbc;

import backend.academy.scrapper.dto.ChatMode;
import backend.academy.scrapper.exception.ChatModeNotFoundException;
import backend.academy.scrapper.repository.jdbc.JdbcChatModeRepository;
import backend.academy.scrapper.service.chat.ChatModeService;
import backend.academy.scrapper.service.model.Mode;
import backend.academy.scrapper.service.update.BatchUpdateService;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
public class JdbcChatModeService implements ChatModeService {

    private final JdbcChatModeRepository chatModeRepository;
    private final BatchUpdateService batchUpdateService;

    @Override
    @Transactional
    public void setMode(Long chatId, String modeName, LocalTime time) {
        if (!chatModeRepository.existModeName(modeName)) {
            chatModeRepository.saveMode(modeName);
        }

        boolean isUpdate = chatModeRepository
            .findByChatId(chatId)
            .map(existing -> {
                chatModeRepository.update(new ChatMode(chatId, modeName, time));
                return true;
            })
            .orElseGet(() -> {
                chatModeRepository.insert(new ChatMode(chatId, modeName, time));
                return false;
            });

        if (isUpdate && modeName.equalsIgnoreCase(Mode.IMMEDIATE.modeName())) {
            batchUpdateService.sendBatchUpdateToUser(chatId);
        }
    }

    @Override
    public ChatMode getMode(Long chatId) {
        return chatModeRepository.findByChatId(chatId).orElseGet(() -> {
            setMode(chatId, Mode.IMMEDIATE.modeName(), null);
            return new ChatMode(chatId, Mode.IMMEDIATE.modeName(), null);
        });
    }

    @Override
    public List<Long> findAllChatIdsByMode(String modeName) {
        return chatModeRepository.findChatIdsByModeName(modeName);
    }

    @Override
    public LocalTime findTimeByChatId(Long chatId) {
        return chatModeRepository.findByChatId(chatId).map(ChatMode::time).orElseThrow(ChatModeNotFoundException::new);
    }
}
