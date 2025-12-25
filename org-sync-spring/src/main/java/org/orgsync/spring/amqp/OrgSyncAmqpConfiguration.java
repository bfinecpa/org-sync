package org.orgsync.spring.amqp;

import org.orgsync.core.engine.SyncEngine;
import org.orgsync.spring.amqp.OrgChartSyncQueueListener;
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
    public MessageConverter orgSyncRabbitMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public OrgChartSyncQueueListener companySyncQueueListener(SyncEngine syncEngine) {
        return new OrgChartSyncQueueListener(syncEngine);
    }
}
