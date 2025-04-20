package backend.academy.bot.kafka;

import backend.academy.bot.dto.LinkUpdate;
import backend.academy.bot.service.LinkNotificationService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import static org.mockito.Mockito.mock;

@TestConfiguration
public class KafkaTestConfig {

    private final BlockingQueue<LinkUpdate> dlqMessages = new LinkedBlockingQueue<>();

    @Bean
    public BlockingQueue<LinkUpdate> dlqMessageQueue() {
        return dlqMessages;
    }

    @KafkaListener(topics = "${app.kafka.updates-topic-name}_dlq", groupId = "dlq-test-group")
    public void listenDlq(LinkUpdate update) {
        dlqMessages.offer(update);
    }

    @Bean
    public LinkNotificationService linkNotificationService() {
        return mock(LinkNotificationService.class);
    }
}
