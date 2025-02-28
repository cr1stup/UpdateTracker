package backend.academy.bot.client.dto;

import jakarta.validation.constraints.NotNull;

public record RemoveLinkRequest(@NotNull Long id) {}
