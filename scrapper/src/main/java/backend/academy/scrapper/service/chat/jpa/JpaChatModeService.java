package backend.academy.scrapper.service.chat.jpa;

import backend.academy.scrapper.dto.ChatMode;
import backend.academy.scrapper.repository.jpa.entity.ChatModeEntity;
import backend.academy.scrapper.repository.jpa.entity.ModeEntity;
import backend.academy.scrapper.repository.jpa.repository.JpaChatModeRepository;
import backend.academy.scrapper.repository.jpa.repository.JpaModeRepository;
import backend.academy.scrapper.service.chat.ChatModeService;
import backend.academy.scrapper.service.model.Mode;
import backend.academy.scrapper.service.update.BatchUpdateService;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Slf4j
public class JpaChatModeService implements ChatModeService {

    private final JpaChatModeRepository chatModeRepository;
    private final JpaModeRepository modeRepository;
    private final BatchUpdateService batchUpdateService;

    @Override
    public void setMode(Long chatId, String modeName, LocalTime time) {
        Mode mode = Mode.fromString(modeName);

        ModeEntity modeEntity = modeRepository.findByName(mode.modeName()).orElseGet(() -> {
            ModeEntity newMode = new ModeEntity();
            newMode.name(mode.modeName());
            return modeRepository.save(newMode);
        });

        var existingChatMode = chatModeRepository.findByChatId(chatId);

        ChatModeEntity chatMode = existingChatMode
            .map(existing -> {
                existing.mode(modeEntity);
                existing.time(time);
                return existing;
            })
            .orElseGet(() -> {
                ChatModeEntity newChatMode = new ChatModeEntity();
                newChatMode.chatId(chatId);
                newChatMode.mode(modeEntity);
                newChatMode.time(time);
                return newChatMode;
            });

        chatModeRepository.save(chatMode);

        if (existingChatMode.isPresent() && modeName.equalsIgnoreCase(Mode.IMMEDIATE.modeName())) {
            batchUpdateService.sendBatchUpdateToUser(chatId);
        }
    }

    @Override
    public ChatMode getMode(Long chatId) {
        log.info("Getting mode: {}", chatId);
        ChatModeEntity chatModeEntity = chatModeRepository.findByChatId(chatId).orElseGet(() -> {
            ChatModeEntity newChatMode = new ChatModeEntity();

            ModeEntity modeEntity = modeRepository
                    .findByName(Mode.IMMEDIATE.modeName())
                    .orElseGet(() -> {
                        ModeEntity newModeEntity = new ModeEntity();
                        newModeEntity.name(Mode.IMMEDIATE.modeName());
                        return modeRepository.save(newModeEntity);
                    });

            newChatMode.chatId(chatId);
            newChatMode.mode(modeEntity);
            newChatMode.time(null);
            return chatModeRepository.save(newChatMode);
        });
        log.info("Current mode: {}", chatModeEntity.mode().name());
        return new ChatMode(chatId, chatModeEntity.mode().name(), chatModeEntity.time());
    }

    @Override
    public List<Long> findAllChatIdsByMode(String modeName) {
        return chatModeRepository.findChatIdsByModeName(modeName);
    }

    @Override
    public LocalTime findTimeByChatId(Long chatId) {
        ChatModeEntity chatModeEntity = chatModeRepository.findByChatId(chatId).orElseThrow(RuntimeException::new);
        return chatModeEntity.time();
    }
}
