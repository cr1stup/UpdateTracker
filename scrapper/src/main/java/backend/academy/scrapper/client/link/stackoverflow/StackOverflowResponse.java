package backend.academy.scrapper.client.link.stackoverflow;

public record StackOverflowResponse(StackOverflowItem[] items) {
    public static final StackOverflowResponse EMPTY = new StackOverflowResponse(null);
}
