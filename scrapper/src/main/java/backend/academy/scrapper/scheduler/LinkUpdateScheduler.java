package backend.academy.scrapper.scheduler;

import backend.academy.scrapper.client.bot.BotClient;
import backend.academy.scrapper.client.link.ClientFactory;
import backend.academy.scrapper.client.link.LinkClient;
import backend.academy.scrapper.config.ScrapperConfig;
import backend.academy.scrapper.dto.Link;
import backend.academy.scrapper.dto.LinkInformation;
import backend.academy.scrapper.dto.LinkUpdate;
import backend.academy.scrapper.service.LinkService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LinkUpdateScheduler {

    private static final Logger logger = LoggerFactory.getLogger(LinkUpdateScheduler.class);

    private final LinkService linkService;
    private final ScrapperConfig scrapperConfig;
    private final ClientFactory clientFactory;
    private final BotClient botClient;

    @Scheduled(fixedDelayString = "#{@'app-backend.academy.scrapper.config.ScrapperConfig'.scheduler.interval}")
    public void update() {
        logger.info("Links update started");
        linkService
                .getListLinkToCheck(scrapperConfig.scheduler().forceCheckDelay())
                .forEach(link -> {
                    logger.info("Updating link {}", link.url());
                    URI url = URI.create(link.url());
                    LinkClient client = clientFactory.getClient(url);
                    LinkInformation linkInformation = client.fetchInformation(url);
                    processLinkInformation(linkInformation, link);
                });
        logger.info("Links update ended");
    }

    private void processLinkInformation(LinkInformation linkInformation, Link link) {
        if (linkInformation.lastModified().isAfter(link.updatedAt())) {
            linkService.update(link.url(), linkInformation.lastModified());
            logger.info("Update on link {}", link.url());
            logger.info("Send update to users");
            botClient.handleUpdates(new LinkUpdate(
                    link.id(), URI.create(link.url()), linkInformation.title(), linkService.getListOfChatId(link)));
        } else {
            linkService.checkNow(link.url());
        }
    }
}
