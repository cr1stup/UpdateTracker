package backend.academy.scrapper.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Link {
    private long id;
    private String url;
    private List<String> tags;
    private List<String> filters;
    private OffsetDateTime updatedAt;
    private OffsetDateTime lastCheckedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Link link)) {
            return false;
        }

        return Objects.equals(url, link.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
