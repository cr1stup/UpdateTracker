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

    private final JpaChatRepository chatRepository;
    private final JpaChatModeService chatModeService;

    @Override
    public void registerChat(Long id) {
        chatRepository.findById(id).ifPresent(chat -> {
            log.info("User [{}] not registered: chat already exists JPA", id);
            throw new ChatAlreadyRegisteredException();
        });
        chatModeService.setDefaultMode(id);
        chatRepository.save(new ChatEntity());
    }

    @Override
    public void deleteChat(Long id) {
        ChatEntity chat = chatRepository.findById(id).orElseThrow(ChatNotFoundException::new);
        chatRepository.delete(chat);
        log.info("Chat [{}] successfully deleted", id);
    }
}
