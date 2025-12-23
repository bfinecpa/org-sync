package org.orgsync.spring.amqp;

import static org.orgsync.core.Constants.ERROR_PREFIX;

import org.orgsync.core.engine.SyncEngine;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.util.StringUtils;

/**
 * Listens for company change events on RabbitMQ and triggers synchronization.
 */
public class RabbitCompanyChangeListener {

    private final SyncEngine syncEngine;

    public RabbitCompanyChangeListener(SyncEngine syncEngine) {
        this.syncEngine = syncEngine;
    }

    @RabbitListener(queues = "${orgsync.amqp.company-change-queue:orgsync.company.changed}")
    public void onCompanyChange(CompanyChangeMessage message) {
        if (message == null || !StringUtils.hasText(message.companyId())) {
            throw new IllegalArgumentException(ERROR_PREFIX + "companyId is required in company change event");
        }
        syncEngine.synchronizeCompany(message.companyId());
    }
}
