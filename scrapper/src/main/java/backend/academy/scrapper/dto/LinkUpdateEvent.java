package backend.academy.scrapper.dto;

import java.time.OffsetDateTime;

public record LinkUpdateEvent(EventInformation information, OffsetDateTime lastModified) {}
