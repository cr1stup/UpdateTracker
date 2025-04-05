package backend.academy.scrapper.client.link.github.dto;

import java.time.OffsetDateTime;

public record GithubResponse(String title, User user, OffsetDateTime created_at, String body) {
    public record User(String login) {}
}
