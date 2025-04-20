package backend.academy.scrapper.redis;

import backend.academy.scrapper.TestcontainersConfiguration;
import backend.academy.scrapper.controller.LinkController;
import backend.academy.scrapper.dto.AddLinkRequest;
import backend.academy.scrapper.dto.LinkResponse;
import backend.academy.scrapper.dto.ListLinksResponse;
import backend.academy.scrapper.dto.RemoveLinkRequest;
import backend.academy.scrapper.service.LinkService;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {LinkController.class})
@EnableAutoConfiguration(exclude = {
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class
})
@Import({TestcontainersConfiguration.class, RedisTestConfig.class})
class LinkControllerCacheTest {

    @Autowired
    private LinkController linkController;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private LinkService linkService;

    private final Long chatId = 12345L;

    private final LinkResponse linkResponse = new LinkResponse(
        1L,
        URI.create("https://example.com"),
        List.of("tag1"),
        List.of("filter1")
    );

    @BeforeEach
    void cleanUp() {
        Mockito.reset(linkService);
    }

    @Test
    @DisplayName("the cache intercepts messages for reading list links")
    void shouldCacheListLinksResponse() {
        ListLinksResponse expectedResponse = new ListLinksResponse(List.of(linkResponse), 1);
        when(linkService.getListLinks(chatId)).thenReturn(expectedResponse);

        ListLinksResponse firstCall = linkController.listLinks(chatId);
        assertEquals(expectedResponse, firstCall);
        verify(linkService, times(1)).getListLinks(chatId);

        ListLinksResponse secondCall = linkController.listLinks(chatId);
        assertEquals(expectedResponse, secondCall);
        verify(linkService, times(1)).getListLinks(chatId);

        assertNotNull(Objects.requireNonNull(cacheManager.getCache("user-links")).get(chatId));
    }

    @Test
    @DisplayName("cache is disabled when add link")
    void shouldEvictCacheOnAddLink() {
        ListLinksResponse initialResponse = new ListLinksResponse(List.of(linkResponse), 1);
        AddLinkRequest request = new AddLinkRequest(URI.create("https://example1.com"), List.of("tag2"), List.of("f2"));
        when(linkService.getListLinks(chatId)).thenReturn(initialResponse);

        linkController.listLinks(chatId);
        assertNotNull(Objects.requireNonNull(cacheManager.getCache("user-links")).get(chatId));

        linkController.addLink(chatId, request);
        assertNull(Objects.requireNonNull(cacheManager.getCache("user-links")).get(chatId));
    }

    @Test
    @DisplayName("cache is disabled when remove link")
    void shouldEvictCacheOnRemoveLink() {
        ListLinksResponse initialResponse = new ListLinksResponse(List.of(linkResponse), 1);
        RemoveLinkRequest request = new RemoveLinkRequest(URI.create("https://example1.com"));
        when(linkService.getListLinks(chatId)).thenReturn(initialResponse);

        linkController.listLinks(chatId);
        assertNotNull(Objects.requireNonNull(cacheManager.getCache("user-links")).get(chatId));

        linkController.removeLink(chatId, request);
        assertNull(Objects.requireNonNull(cacheManager.getCache("user-links")).get(chatId));
    }
}
