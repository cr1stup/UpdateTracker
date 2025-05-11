package backend.academy.bot.config;

import backend.academy.bot.client.ScrapperClient;
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
@EnableConfigurationProperties(ClientProperties.class)
public class ScrapperClientConfig {

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder, ClientProperties properties) {
        HttpClient httpClient = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) properties.timeout().connect().toMillis())
            .responseTimeout(Duration.ofMillis(properties.timeout().response().toMillis()))
            .doOnConnected(conn -> conn
                .addHandlerLast(new ReadTimeoutHandler(properties.timeout().read().toMillis(), TimeUnit.MILLISECONDS))
                .addHandlerLast(new WriteTimeoutHandler(properties.timeout().write().toMillis(), TimeUnit.MILLISECONDS)));

        return webClientBuilder
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .defaultStatusHandler(httpStatusCode -> true, clientResponse -> Mono.empty())
            .defaultHeader("Content-Type", "application/json")
            .baseUrl(properties.scrapperUrl())
            .build();
    }

    @Bean
    public ScrapperClient scrapperClient(WebClient webClient) {
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient))
                .build();

        return factory.createClient(ScrapperClient.class);
    }
}
