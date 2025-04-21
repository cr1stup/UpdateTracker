package backend.academy.scrapper.service.chat.jdbc;

import backend.academy.scrapper.exception.ChatAlreadyRegisteredException;
import backend.academy.scrapper.exception.ChatNotFoundException;
import backend.academy.scrapper.repository.jdbc.JdbcChatRepository;
import backend.academy.scrapper.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class JdbcChatService implements ChatService {

    private final JdbcChatRepository repository;
    private final JdbcChatModeService chatModeService;

    @Override
    public void registerChat(Long id) {
        if (repository.isExists(id)) {
            log.info("user [{}] not registered: chat already registered", id);
            throw new ChatAlreadyRegisteredException();
        }
        chatModeService.setDefaultMode(id);
        repository.add(id);
    }

    @Override
    public void deleteChat(Long id) {
        if (!repository.isExists(id)) {
            throw new ChatNotFoundException();
        }
        repository.remove(id);
    }
}
