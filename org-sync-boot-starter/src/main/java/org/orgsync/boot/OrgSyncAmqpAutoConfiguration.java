package org.orgsync.boot;

import org.orgsync.spring.amqp.OrgSyncAmqpConfiguration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Import;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.orgsync.core.engine.SyncEngine;

/**
 * Auto-configuration that connects RabbitMQ company change events to the sync engine.
 */
@AutoConfiguration
@AutoConfigureAfter(RabbitAutoConfiguration.class)
@ConditionalOnClass({EnableRabbit.class, RabbitListener.class})
@ConditionalOnBean({ConnectionFactory.class, SyncEngine.class})
@Import(OrgSyncAmqpConfiguration.class)
public class OrgSyncAmqpAutoConfiguration {
}
