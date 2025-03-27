package backend.academy.scrapper.scheduler;

import backend.academy.scrapper.dto.Link;
import java.time.OffsetDateTime;
import java.util.List;

public class LinkFixtures {
    private static final OffsetDateTime NOW = OffsetDateTime.now();

    public static Link getLink1() {
        return new Link(
                1L, "https://github.com/example/repo1", List.of("tag1"), List.of("filter1"), NOW, NOW.minusMinutes(20));
    }

    public static Link getLink2() {
        return new Link(
                2L, "https://github.com/example/repo2", List.of("tag2"), List.of("filter2"), NOW, NOW.minusMinutes(40));
    }

    public static List<Long> getChatIds1() {
        return List.of(101L, 102L);
    }

    public static List<Long> getChatIds2() {
        return List.of(103L);
    }
}
