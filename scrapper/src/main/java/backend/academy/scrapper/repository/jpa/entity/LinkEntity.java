package backend.academy.scrapper.repository.jpa.entity;

import backend.academy.scrapper.dto.Link;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "link")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LinkEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String url;

    @Column(nullable = false)
    private String description;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "last_checked_at")
    private OffsetDateTime lastCheckedAt;

    @OneToMany(mappedBy = "link", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ChatLinkEntity> chatLinks = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "links")
    private Set<ChatEntity> chats = new HashSet<>();

    public Link toDto() {
        return new Link(id, url, description, updatedAt, lastCheckedAt);
    }
}
