package backend.academy.bot.client.dto;

import java.util.List;

public record ListLinksResponse(List<LinkResponse> links, Integer size) {}
