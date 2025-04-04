package backend.academy.scrapper.dto;

import java.time.OffsetDateTime;

public record LinkUpdateEvent(String information, OffsetDateTime lastModified) {}
