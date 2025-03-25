package backend.academy.scrapper.service;

import backend.academy.scrapper.exception.ChatAlreadyRegisteredException;
import backend.academy.scrapper.exception.ChatNotFoundException;
import backend.academy.scrapper.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultChatService implements ChatService {

    private final ChatRepository chatRepository;

    @Override
    public void registerChat(Long id) {
        if (chatRepository.isChatExist(id)) {
            log.info("user [{}] not registered: chat already registered", id);
            throw new ChatAlreadyRegisteredException();
        }
        chatRepository.addChat(id);
    }

    @Override
    public void deleteChat(Long id) {
        if (!chatRepository.isChatExist(id)) {
            throw new ChatNotFoundException();
        }
        chatRepository.deleteChat(id);
    }
}
