package backend.academy.scrapper.controller;

import backend.academy.scrapper.dto.ChatMode;
import backend.academy.scrapper.service.ChatModeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/mode", consumes = "application/json", produces = "application/json")
@RequiredArgsConstructor
public class ChatModeController {

    private final ChatModeService chatModeService;

    @PostMapping
    public void setChatMode(@RequestHeader(name = "Tg-Chat-Id") Long id,
                            @RequestBody @Valid ChatMode mode) {
        chatModeService.setMode(id, mode.name(), mode.time());
    }

    @GetMapping
    public ChatMode getChatMode(@RequestHeader(name = "Tg-Chat-Id") Long id) {
        return chatModeService.getMode(id);
    }
}
