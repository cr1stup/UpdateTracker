package backend.academy.scrapper.config;

import backend.academy.scrapper.client.bot.BotClient;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Configuration
@EnableConfigurationProperties({ClientProperties.class, RetryProperties.class})
public class ClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder, ClientProperties clientProperties, RetryProperties retryProperties) {
        HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) clientProperties.timeout().connect().toMillis())
            .responseTimeout(Duration.ofMillis(clientProperties.timeout().response().toMillis()))
            .doOnConnected(conn -> conn
                .addHandlerLast(new ReadTimeoutHandler(
                    clientProperties.timeout().read().toMillis(),
                    TimeUnit.MILLISECONDS))
                .addHandlerLast(new WriteTimeoutHandler(
                    clientProperties.timeout().write().toMillis(),
                    TimeUnit.MILLISECONDS)));

        return webClientBuilder
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .defaultStatusHandler(httpStatusCode -> true, clientResponse -> Mono.empty())
            .defaultHeader("Content-Type", "application/json")
            .baseUrl(clientProperties.botUrl())
            .filter(RetryConfig.createFilter(retryProperties))
            .build();
    }

    @Bean
    public BotClient botClient(WebClient webClient) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient))
                .build();

        return factory.createClient(BotClient.class);
    }
}
