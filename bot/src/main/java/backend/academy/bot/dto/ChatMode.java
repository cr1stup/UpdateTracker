package backend.academy.bot.dto;

import java.time.LocalTime;

public record ChatMode(Long chatId, String name, LocalTime time) {}
