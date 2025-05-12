package backend.academy.scrapper.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.util.List;
import java.util.function.Predicate;

import java.util.List;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

@Slf4j
public class RetryConfig {

    public static ExchangeFilterFunction createFilter(RetryProperties properties) {
        return (response, next) -> next.exchange(response)
            .flatMap(clientResponse -> {
                if (clientResponse.statusCode().isError()
                    && properties.retryableCodes().contains(clientResponse.statusCode().value())) {
                    return clientResponse.createError();
                } else {
                    return Mono.just(clientResponse);
                }
            }).retryWhen(createRetry(properties));
    }

    private static Retry createRetry(RetryProperties properties) {
        return RetryBackoffSpec.fixedDelay(properties.maxAttempts(), properties.waitDuration())
            .filter(buildErrorFilter(properties.retryableCodes()))
            .doBeforeRetry(retrySignal -> {
                Throwable failure = retrySignal.failure();
                long attempt = retrySignal.totalRetries() + 1;
                log.warn("Retry attempt #{} due to: {}", attempt, failure.getMessage());
            });
    }

    private static Predicate<Throwable> buildErrorFilter(List<Integer> retryCodes) {
        return retrySignal -> {
            if (retrySignal instanceof WebClientResponseException e) {
                return retryCodes.contains(e.getStatusCode().value());
            }
            return true;
        };
    }
}
