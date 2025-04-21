package backend.academy.scrapper.dto;

import java.time.LocalTime;

public record ChatMode(Long chatId, String name, LocalTime time) {}
