package backend.academy.scrapper.service.jpa;

import backend.academy.scrapper.exception.ChatAlreadyRegisteredException;
import backend.academy.scrapper.exception.ChatNotFoundException;
import backend.academy.scrapper.repository.jpa.entity.ChatEntity;
import backend.academy.scrapper.repository.jpa.repository.JpaChatRepository;
import backend.academy.scrapper.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
public class JpaChatService implements ChatService {

    private final JpaChatRepository jpaChatRepository;

    @Override
    public void registerChat(Long id) {
        jpaChatRepository.findById(id).ifPresent(chat -> {
            log.info("User [{}] not registered: chat already exists JPA", id);
            throw new ChatAlreadyRegisteredException();
        });

        jpaChatRepository.save(new ChatEntity());
    }

    @Override
    public void deleteChat(Long id) {
        ChatEntity chat = jpaChatRepository.findById(id).orElseThrow(ChatNotFoundException::new);
        jpaChatRepository.delete(chat);
        log.info("Chat [{}] successfully deleted", id);
    }
}
