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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LinkUpdateScheduler {

    private final LinkService linkService;
    private final ScrapperConfig scrapperConfig;
    private final ClientFactory clientFactory;
    private final BotClient botClient;

    @Scheduled(fixedDelayString = "#{@'app-backend.academy.scrapper.config.ScrapperConfig'.scheduler.interval}")
    public void update() {
        linkService
                .getListLinkToCheck(scrapperConfig.scheduler().forceCheckDelay())
                .forEach(link -> {
                    URI url = URI.create(link.url());
                    LinkClient client = clientFactory.getClient(url);
                    LinkInformation linkInformation = client.fetchInformation(url);
                    processLinkInformation(linkInformation, link);
                });
    }

    private void processLinkInformation(LinkInformation linkInformation, Link link) {
        if (linkInformation.lastModified().isAfter(link.updatedAt())) {
            linkService.update(link.url(), linkInformation.lastModified());
            botClient.handleUpdates(new LinkUpdate(
                    link.id(), URI.create(link.url()), linkInformation.title(), linkService.getListOfChatId(link)));
        } else {
            linkService.checkNow(link.url());
        }
    }
}
