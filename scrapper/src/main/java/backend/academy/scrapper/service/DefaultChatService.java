package backend.academy.scrapper.service;

import backend.academy.scrapper.exception.ChatAlreadyRegisteredException;
import backend.academy.scrapper.exception.ChatNotFoundException;
import backend.academy.scrapper.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultChatService implements ChatService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultChatService.class);

    private final ChatRepository chatRepository;

    @Override
    public void registerChat(Long id) {
        if (chatRepository.isChatExist(id)) {
            logger.info("user [{}] not registered: chat already registered", id);
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
