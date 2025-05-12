package backend.academy.scrapper.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import backend.academy.scrapper.config.ScrapperConfig;
import backend.academy.scrapper.dto.LinkUpdate;
import backend.academy.scrapper.service.update.FallbackUpdateService;
import backend.academy.scrapper.service.update.ImmediateUpdateService;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FallbackUpdateServiceTest {

    @Mock
    private ImmediateUpdateService kafkaService;

    @Mock
    private ImmediateUpdateService httpService;

    @Mock
    private ScrapperConfig config;

    private FallbackUpdateService fallbackService;
    private final LinkUpdate update = new LinkUpdate(1L, URI.create("https//example.com"), "desc", List.of(1L), false);

    @BeforeEach
    void setUp() {
        when(kafkaService.transportName()).thenReturn("kafka");
        when(httpService.transportName()).thenReturn("http");

        fallbackService = new FallbackUpdateService(List.of(kafkaService, httpService), config);
        fallbackService.init();
    }

    @Test
    @DisplayName("should use Kafka transport when it's first in priority order")
    void shouldUsePrimaryKafkaTransport() {
        when(config.transportOrder()).thenReturn(List.of("kafka", "http"));

        fallbackService.sendUpdatesToUsers(update);

        verify(kafkaService).sendUpdatesToUsers(update);
        verify(httpService, never()).sendUpdatesToUsers(any());
    }

    @Test
    @DisplayName("should fallback to HTTP when Kafka transport fails")
    void shouldFallbackToHttpWhenKafkaFails() {
        when(config.transportOrder()).thenReturn(List.of("kafka", "http"));
        doThrow(new RuntimeException("Kafka down")).when(kafkaService).sendUpdatesToUsers(update);

        fallbackService.sendUpdatesToUsers(update);

        verify(httpService).sendUpdatesToUsers(update);
    }
}
