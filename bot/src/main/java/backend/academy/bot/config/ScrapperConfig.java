package backend.academy.bot.config;

import backend.academy.bot.client.ScrapperClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

@Configuration
public class ScrapperConfig {

    @Value("${scrapper.url}")
    private String scrapperUrl;

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
                .defaultStatusHandler(httpStatusCode -> true, clientResponse -> Mono.empty())
                .defaultHeader("Content-Type", "application/json")
                .baseUrl(scrapperUrl)
                .build();
    }

    @Bean
    public ScrapperClient scrapperClient(WebClient webClient) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
            .builderFor(WebClientAdapter.create(webClient))
            .build();

        return factory.createClient(ScrapperClient.class);
    }
}
