package backend.academy.scrapper.client.link.stackoverflow.dto;

import java.util.List;

public record StackOverflowResponse(List<StackOverflowItem> items) {
    public static final StackOverflowResponse EMPTY = new StackOverflowResponse(null);
}
