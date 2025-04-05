package backend.academy.scrapper.config;

import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class TaskExecutorConfig {

    @Bean
    public ThreadPoolTaskExecutor linkUpdateTaskExecutor(ScrapperConfig config) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(config.scheduler().threadCount());
        executor.setMaxPoolSize(config.scheduler().threadCount());
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("LinkUpdate-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
