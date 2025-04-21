package backend.academy.scrapper.config;

import backend.academy.scrapper.client.link.ClientFactory;
import backend.academy.scrapper.repository.jdbc.JdbcChatLinkRepository;
import backend.academy.scrapper.repository.jdbc.JdbcChatModeRepository;
import backend.academy.scrapper.repository.jdbc.JdbcChatRepository;
import backend.academy.scrapper.repository.jdbc.JdbcFilterRepository;
import backend.academy.scrapper.repository.jdbc.JdbcLinkRepository;
import backend.academy.scrapper.repository.jdbc.JdbcTagRepository;
import backend.academy.scrapper.repository.jpa.repository.JpaChatLinkRepository;
import backend.academy.scrapper.repository.jpa.repository.JpaChatModeRepository;
import backend.academy.scrapper.repository.jpa.repository.JpaChatRepository;
import backend.academy.scrapper.repository.jpa.repository.JpaFilterRepository;
import backend.academy.scrapper.repository.jpa.repository.JpaLinkRepository;
import backend.academy.scrapper.repository.jpa.repository.JpaModeRepository;
import backend.academy.scrapper.repository.jpa.repository.JpaTagRepository;
import backend.academy.scrapper.service.BatchUpdateService;
import backend.academy.scrapper.service.ChatService;
import backend.academy.scrapper.service.LinkService;
import backend.academy.scrapper.service.jdbc.JdbcChatModeService;
import backend.academy.scrapper.service.jdbc.JdbcChatService;
import backend.academy.scrapper.service.jdbc.JdbcLinkService;
import backend.academy.scrapper.service.jpa.JpaChatModeService;
import backend.academy.scrapper.service.jpa.JpaChatService;
import backend.academy.scrapper.service.jpa.JpaLinkService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfig {

    @Bean
    @ConditionalOnProperty(name = "app.database-access-type", havingValue = "jdbc")
    public ChatService jdbcChatService(JdbcChatRepository chatRepository, JdbcChatModeService jdbcChatModeService) {
        return new JdbcChatService(chatRepository, jdbcChatModeService);
    }

    @Bean
    @ConditionalOnProperty(name = "app.database-access-type", havingValue = "jdbc")
    public LinkService jdbcLinkService(
            ClientFactory clientFactory,
            JdbcChatLinkRepository chatLinkRepository,
            JdbcChatRepository chatRepository,
            JdbcLinkRepository linkRepository,
            JdbcTagRepository tagRepository,
            JdbcFilterRepository filterRepository) {
        return new JdbcLinkService(
                clientFactory, chatLinkRepository, chatRepository, linkRepository, tagRepository, filterRepository);
    }

    @Bean
    @ConditionalOnProperty(name = "app.database-access-type", havingValue = "jpa")
    public ChatService jpaChatService(JpaChatRepository jpaChatRepository, JpaChatModeService jpaChatModeService) {
        return new JpaChatService(jpaChatRepository, jpaChatModeService);
    }

    @Bean
    @ConditionalOnProperty(name = "app.database-access-type", havingValue = "jpa")
    public LinkService jpaLinkService(
            ClientFactory clientFactory,
            JpaChatLinkRepository chatLinkRepository,
            JpaChatRepository chatRepository,
            JpaLinkRepository linkRepository,
            JpaTagRepository tagRepository,
            JpaFilterRepository filterRepository) {
        return new JpaLinkService(
                clientFactory, chatLinkRepository, chatRepository, linkRepository, tagRepository, filterRepository);
    }

    @Bean
    @ConditionalOnProperty(name = "app.database-access-type", havingValue = "jpa")
    public JpaChatModeService jpaChatModeService(JpaChatModeRepository chatModeRepository, JpaModeRepository modeRepository, BatchUpdateService batchUpdateService) {
        return new JpaChatModeService(chatModeRepository, modeRepository, batchUpdateService);
    }

    @Bean
    @ConditionalOnProperty(name = "app.database-access-type", havingValue = "jdbc")
    public JdbcChatModeService jdbcChatModeService(JdbcChatModeRepository chatModeRepository, BatchUpdateService batchUpdateService) {
        return new JdbcChatModeService(chatModeRepository, batchUpdateService);
    }
}
