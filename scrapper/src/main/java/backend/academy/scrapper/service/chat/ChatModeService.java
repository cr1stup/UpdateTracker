package backend.academy.scrapper.service.chat;

import backend.academy.scrapper.dto.ChatMode;
import java.time.LocalTime;
import java.util.List;

public interface ChatModeService {
    void setMode(Long chatId, String modeName, LocalTime time);

    ChatMode getMode(Long chatId);

    List<Long> findAllChatIdsByMode(String modeName);

    LocalTime findTimeByChatId(Long chatId);
}
