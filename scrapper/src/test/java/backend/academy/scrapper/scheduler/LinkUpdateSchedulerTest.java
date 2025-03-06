package backend.academy.scrapper.scheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.client.bot.BotClient;
import backend.academy.scrapper.client.link.ClientFactory;
import backend.academy.scrapper.client.link.LinkClient;
import backend.academy.scrapper.config.ScrapperConfig;
import backend.academy.scrapper.dto.Link;
import backend.academy.scrapper.dto.LinkInformation;
import backend.academy.scrapper.service.DefaultLinkService;
import java.net.URI;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LinkUpdateSchedulerTest {

    @Mock
    private DefaultLinkService linkService;

    @Mock
    private ScrapperConfig appConfig;

    @Mock
    private ClientFactory clientFactory;

    @Mock
    private BotClient botClient;

    @InjectMocks
    private LinkUpdateScheduler linkUpdateScheduler;

    private static final Link LINK_1 = new Link(
            1L,
            "https://github.com/example/repo1",
            List.of("tag1"),
            List.of("filter1"),
            OffsetDateTime.now(),
            OffsetDateTime.now().minusMinutes(20));
    private static final Link LINK_2 = new Link(
            2L,
            "https://github.com/example/repo2",
            List.of("tag2"),
            List.of("filter2"),
            OffsetDateTime.now(),
            OffsetDateTime.now().minusMinutes(40));
    private static final List<Long> CHAT_IDS_1 = List.of(101L, 102L);
    private static final List<Long> CHAT_IDS_2 = List.of(103L);

    @Test
    @DisplayName("send updates only to tracked users")
    void testSendUpdateOnlyToTrackedUsers() {
        mockSchedulerConfig();
        mockLinkService();
        mockClientFactory();

        linkUpdateScheduler.update();

        verifyBotClientUpdates(LINK_1, CHAT_IDS_1);
        verifyBotClientUpdates(LINK_2, CHAT_IDS_2);
    }

    private void verifyBotClientUpdates(Link link, List<Long> chatIds) {
        verify(botClient)
                .handleUpdates(argThat(update -> update.id().equals(link.id())
                        && update.url().equals(URI.create(link.url()))
                        && chatIds.equals(update.tgChatIds())));
    }

    private void mockSchedulerConfig() {
        ScrapperConfig.Scheduler schedulerConfig = mock(ScrapperConfig.Scheduler.class);
        when(appConfig.scheduler()).thenReturn(schedulerConfig);
        when(schedulerConfig.forceCheckDelay()).thenReturn(Duration.ofMinutes(30));
    }

    private void mockLinkService() {
        when(linkService.getListLinkToCheck(any(Duration.class))).thenReturn(List.of(LINK_1, LINK_2));
        when(linkService.getListOfChatId(LINK_1)).thenReturn(CHAT_IDS_1);
        when(linkService.getListOfChatId(LINK_2)).thenReturn(CHAT_IDS_2);
    }

    private void mockClientFactory() {
        LinkClient client = mock(LinkClient.class);
        when(clientFactory.getClient(any(URI.class))).thenReturn(client);

        LinkInformation linkInfo1 =
                new LinkInformation(URI.create(LINK_1.url()), "Repo1", "Description1", OffsetDateTime.now());
        LinkInformation linkInfo2 =
                new LinkInformation(URI.create(LINK_2.url()), "Repo2", "Description2", OffsetDateTime.now());

        when(client.fetchInformation(URI.create(LINK_1.url()))).thenReturn(linkInfo1);
        when(client.fetchInformation(URI.create(LINK_2.url()))).thenReturn(linkInfo2);
    }
}
