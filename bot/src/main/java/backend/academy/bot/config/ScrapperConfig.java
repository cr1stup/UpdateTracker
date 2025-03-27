package backend.academy.bot.config;

import backend.academy.bot.client.ScrapperClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

@Configuration
@EnableConfigurationProperties(ScrapperProperties.class)
@RequiredArgsConstructor
public class ScrapperConfig {

    private final ScrapperProperties properties;

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .defaultStatusHandler(httpStatusCode -> true, clientResponse -> Mono.empty())
                .defaultHeader("Content-Type", "application/json")
                .baseUrl(properties.url())
                .build();
    }

    @Bean
    public ScrapperClient scrapperClient(WebClient webClient) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient))
                .build();

        return factory.createClient(ScrapperClient.class);
    }
}
