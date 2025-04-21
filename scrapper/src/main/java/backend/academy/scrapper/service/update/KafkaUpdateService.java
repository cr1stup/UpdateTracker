package backend.academy.scrapper.service.update;

import backend.academy.scrapper.config.ScrapperConfig;
import backend.academy.scrapper.dto.LinkUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.message-transport", havingValue = "kafka")
public class KafkaUpdateService implements ImmediateUpdateService {

    private final KafkaTemplate<String, LinkUpdate> kafkaTemplate;
    private final ScrapperConfig scrapperConfig;

    @Override
    public void sendUpdatesToUsers(LinkUpdate linkUpdate) {
        kafkaTemplate.send(scrapperConfig.kafka().updatesTopicName(), linkUpdate);
    }
}
