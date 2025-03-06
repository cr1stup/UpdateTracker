package backend.academy.scrapper.controller;

import backend.academy.scrapper.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tg-chat/{id}")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public void registerChat(@PathVariable Long id) {
        chatService.registerChat(id);
    }

    @DeleteMapping
    public void deleteChat(@PathVariable Long id) {
        chatService.deleteChat(id);
    }
}
