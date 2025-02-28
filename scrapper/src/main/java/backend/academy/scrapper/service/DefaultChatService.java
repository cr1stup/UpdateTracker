package backend.academy.scrapper.service;

import backend.academy.scrapper.repository.ChatRepository;
import org.springframework.stereotype.Component;

@Component
public class DefaultChatService implements ChatService {

    @Override
    public void registerChat(Long id) {
        ChatRepository.add(id);
    }

    @Override
    public void deleteChat(Long id) {
        ChatRepository.delete(id);
    }
}
