package backend.academy.bot.kafka;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;

import backend.academy.bot.TestcontainersConfiguration;
import backend.academy.bot.dto.LinkUpdate;
import backend.academy.bot.service.LinkNotificationService;
import java.net.URI;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@Import({TestcontainersConfiguration.class, KafkaTestConfig.class})
@TestPropertySource(properties = {"spring.kafka.consumer.auto-offset-reset=earliest"})
class KafkaUpdatesListenerTest {

    @Autowired
    private LinkNotificationService linkNotificationService;

    @Autowired
    private KafkaTemplate<String, LinkUpdate> kafkaTemplate;

    @Autowired
    private BlockingQueue<LinkUpdate> dlqMessageQueue;

    @BeforeEach
    void cleanUp() {
        dlqMessageQueue.clear();
        Mockito.reset(linkNotificationService);
    }

    @Test
    @DisplayName("kafka processes a valid message")
    void testValidMessageIsProcessed() {
        LinkUpdate update = new LinkUpdate(1L, URI.create("https://example.com"), "desc", List.of(123L), false);

        kafkaTemplate.send("updates", update);

        await().atMost(5, SECONDS).untilAsserted(() -> Mockito.verify(linkNotificationService, times(1))
                .notifyLinkUpdate(Mockito.any(LinkUpdate.class)));
    }

    @Test
    @DisplayName("kafka sends invalid message to dlq")
    void testInvalidMessageGoesToDlq() {
        LinkUpdate update = new LinkUpdate(1L, URI.create("https://example.com"), "desc", null, false);
        Mockito.doThrow(RuntimeException.class).when(linkNotificationService).notifyLinkUpdate(update);

        kafkaTemplate.send("updates", update);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            LinkUpdate dlqMessage = dlqMessageQueue.poll();
            assertNotNull(dlqMessage);
        });
    }
}
