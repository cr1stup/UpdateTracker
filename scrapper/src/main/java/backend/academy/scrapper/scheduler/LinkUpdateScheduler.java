package backend.academy.scrapper.scheduler;

import backend.academy.scrapper.client.link.ClientFactory;
import backend.academy.scrapper.client.link.LinkClient;
import backend.academy.scrapper.config.ScrapperConfig;
import backend.academy.scrapper.dto.Link;
import backend.academy.scrapper.dto.LinkInformation;
import backend.academy.scrapper.dto.LinkUpdate;
import backend.academy.scrapper.service.LinkService;
import backend.academy.scrapper.service.UpdateService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LinkUpdateScheduler {

    private final LinkService linkService;
    private final ScrapperConfig scrapperConfig;
    private final ClientFactory clientFactory;
    private final UpdateService updateService;

    @Scheduled(fixedDelayString = "#{@'app-backend.academy.scrapper.config.ScrapperConfig'.scheduler.interval}")
    public void update() {
        log.info("Links update started");
        linkService
                .getListLinkToCheck(scrapperConfig.scheduler().forceCheckDelay())
                .forEach(link -> {
                    log.info("Updating link {}", link.url());
                    URI url = URI.create(link.url());
                    LinkClient client = clientFactory.getClient(url);
                    LinkInformation linkInformation = client.fetchInformation(url);
                    processLinkInformation(linkInformation, link);
                });
        log.info("Links update ended");
    }

    private void processLinkInformation(LinkInformation linkInformation, Link link) {
        if (linkInformation.lastModified().isAfter(link.updatedAt())) {
            linkService.update(link.url(), linkInformation.lastModified());
            log.info("Update on link {}", link.url());
            log.info("Send update to users");
            updateService.sendUpdatesToUsers(new LinkUpdate(
                link.id(), URI.create(link.url()), linkInformation.title(), linkService.getListOfChatId(link)));
            return;
        }

        linkService.checkNow(link.url());
    }
}
