package backend.academy.scrapper.scheduler;

import backend.academy.scrapper.client.link.ClientFactory;
import backend.academy.scrapper.client.link.LinkClient;
import backend.academy.scrapper.config.ScrapperConfig;
import backend.academy.scrapper.dto.EventInformation;
import backend.academy.scrapper.dto.Link;
import backend.academy.scrapper.dto.LinkInformation;
import backend.academy.scrapper.dto.LinkUpdate;
import backend.academy.scrapper.service.DispatcherUpdatesService;
import backend.academy.scrapper.service.LinkService;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LinkUpdateScheduler {

    private final LinkService linkService;
    private final ScrapperConfig config;
    private final ClientFactory clientFactory;
    private final ThreadPoolTaskExecutor linkUpdateTaskExecutor;
    private final DispatcherUpdatesService dispatcherUpdatesService;

    @Scheduled(fixedDelayString = "#{@'app-backend.academy.scrapper.config.ScrapperConfig'.scheduler.interval}")
    public void update() {
        log.info("Links update started");

        List<Link> linksToCheck = linkService.getListLinkToCheck(
                config.scheduler().forceCheckDelay(), config.scheduler().batchSize());

        List<CompletableFuture<Void>> futures = linksToCheck.stream()
                .map(link -> CompletableFuture.runAsync(() -> processLink(link), linkUpdateTaskExecutor))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .exceptionally(ex -> {
                    log.info("Error during link processing", ex);
                    return null;
                })
                .join();

        log.info("Links update completed. Processed {} links", linksToCheck.size());
    }

    private void processLink(Link link) {
        try {
            log.info("Processing link {} with id {}", link.url(), link.id());
            URI url = URI.create(link.url());
            LinkClient client = clientFactory.getClient(url);
            LinkInformation linkInformation = client.fetchInformation(url);
            processLinkInformation(linkInformation, link);
        } catch (Exception e) {
            log.info("Error processing link {}: {}", link.url(), e.getMessage());
        }
    }

    private void processLinkInformation(LinkInformation linkInformation, Link link) {
        linkInformation.events().stream()
                .filter(event -> event.lastModified().isAfter(link.updatedAt()))
                .findFirst()
                .ifPresentOrElse(
                        event -> {
                            linkService.update(link.id(), event.lastModified());
                            log.info("Detected update for link {}", link.url());
                            EventInformation eventInformation = event.information();
                            log.info("Update from user {}", eventInformation.user());
                            dispatcherUpdatesService.dispatchUpdates(new LinkUpdate(
                                    link.id(),
                                    URI.create(link.url()),
                                    eventInformation.getFormattedInformation(),
                                    linkService.getListOfChatId(link.id(), eventInformation.user()), false));
                        },
                        () -> linkService.checkNow(link.id()));
    }
}
