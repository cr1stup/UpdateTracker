package backend.academy.bot.dto;

import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.util.List;

public record LinkUpdate(
        @NotNull Long id,
        URI url,
        @NotNull String description,
        @NotNull List<Long> tgChatIds,
        @NotNull Boolean isBatch) {}
