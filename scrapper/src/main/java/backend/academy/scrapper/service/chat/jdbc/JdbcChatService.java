package backend.academy.scrapper.service.chat.jdbc;

import backend.academy.scrapper.exception.ChatAlreadyRegisteredException;
import backend.academy.scrapper.exception.ChatNotFoundException;
import backend.academy.scrapper.repository.jdbc.JdbcChatRepository;
import backend.academy.scrapper.service.chat.ChatService;
import backend.academy.scrapper.service.model.Mode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class JdbcChatService implements ChatService {

    private final JdbcChatRepository chatRepository;
    private final JdbcChatModeService chatModeService;

    @Override
    public void registerChat(Long chatId) {
        if (chatRepository.isExists(chatId)) {
            log.info("user [{}] not registered: chat already registered", chatId);
            throw new ChatAlreadyRegisteredException();
        }
        chatRepository.add(chatId);
        chatModeService.setMode(chatId, Mode.IMMEDIATE.modeName(), null);
    }

    @Override
    public void deleteChat(Long id) {
        if (!chatRepository.isExists(id)) {
            throw new ChatNotFoundException();
        }
        chatRepository.remove(id);
    }
}
