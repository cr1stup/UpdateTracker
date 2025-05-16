package backend.academy.scrapper.service.chat.jpa;

import backend.academy.scrapper.exception.ChatAlreadyRegisteredException;
import backend.academy.scrapper.exception.ChatNotFoundException;
import backend.academy.scrapper.repository.jpa.entity.ChatEntity;
import backend.academy.scrapper.repository.jpa.repository.JpaChatRepository;
import backend.academy.scrapper.service.chat.ChatService;
import backend.academy.scrapper.service.model.Mode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
public class JpaChatService implements ChatService {

    private final JpaChatRepository chatRepository;
    private final JpaChatModeService chatModeService;

    @Override
    public void registerChat(Long chatId) {
        chatRepository.findById(chatId).ifPresent(chat -> {
            log.info("User [{}] not registered: chat already exists JPA", chatId);
            throw new ChatAlreadyRegisteredException();
        });
        chatRepository.save(new ChatEntity().id(chatId));
        chatModeService.setMode(chatId, Mode.IMMEDIATE.modeName(), null);
    }

    @Override
    public void deleteChat(Long id) {
        ChatEntity chat = chatRepository.findById(id).orElseThrow(ChatNotFoundException::new);
        chatRepository.delete(chat);
        log.info("Chat [{}] successfully deleted", id);
    }
}
