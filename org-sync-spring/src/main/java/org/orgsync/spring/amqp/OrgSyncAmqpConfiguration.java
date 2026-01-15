package org.orgsync.spring.amqp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.orgsync.core.engine.SyncEngine;
import org.orgsync.core.logging.SyncLogger;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires RabbitMQ listener infrastructure for org-sync.
 */
@Configuration
@EnableRabbit
public class OrgSyncAmqpConfiguration {

    @Bean
    public MessageConverter orgSyncRabbitMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public OrgChartSyncQueueListener companySyncQueueListener(SyncEngine syncEngine,
                                                              ObjectMapper objectMapper,
                                                              SyncLogger syncLogger) {
        return new OrgChartSyncQueueListener(syncEngine, objectMapper, syncLogger);
    }
}
