package backend.academy.scrapper.service;

import backend.academy.scrapper.exception.ChatAlreadyRegisteredException;
import backend.academy.scrapper.exception.ChatNotFoundException;
import backend.academy.scrapper.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultChatService implements ChatService {

    private final ChatRepository chatRepository;

    @Override
    public void registerChat(Long id) {
        if (chatRepository.isChatExist(id)) {
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
