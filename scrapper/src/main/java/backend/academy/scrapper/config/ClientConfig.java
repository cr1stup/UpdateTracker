package backend.academy.scrapper.config;

import backend.academy.scrapper.client.bot.BotClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

@Configuration
@EnableScheduling
@EnableConfigurationProperties(ClientProperties.class)
@RequiredArgsConstructor
public class ClientConfig {

    private final ClientProperties properties;

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .defaultStatusHandler(httpStatusCode -> true, clientResponse -> Mono.empty())
                .defaultHeader("Content-Type", "application/json")
                .baseUrl(properties.url())
                .build();
    }

    @Bean
    public BotClient botClient(WebClient webClient) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient))
                .build();

        return factory.createClient(BotClient.class);
    }
}
