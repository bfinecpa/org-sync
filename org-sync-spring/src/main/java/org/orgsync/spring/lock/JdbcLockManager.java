package org.orgsync.spring.lock;

import java.sql.ResultSet;
import org.orgsync.core.lock.LockManager;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Map;
import java.util.Objects;

/**
 * Acquires a company-level lock using a database row lock (SELECT ... FOR UPDATE).
 */
public class JdbcLockManager implements LockManager {

    private final TransactionTemplate transactionTemplate;
    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final String lockSql;

    public JdbcLockManager(TransactionTemplate transactionTemplate,
                           NamedParameterJdbcTemplate jdbcTemplate,
                           String companyTableName,
                           String companyUuidColumn) {
        this.transactionTemplate = Objects.requireNonNull(transactionTemplate, "transactionTemplate");
        this.jdbcTemplate = Objects.requireNonNull(jdbcTemplate, "jdbcTemplate");
        if (companyTableName == null || companyTableName.isBlank()) {
            throw new IllegalArgumentException("[org-sync] companyTableName must not be blank");
        }
        if (companyUuidColumn == null || companyUuidColumn.isBlank()) {
            throw new IllegalArgumentException("[org-sync] companyUuidColumn must not be blank");
        }
        this.lockSql = String.format(
                "SELECT %s FROM %s WHERE %s = :companyUuid FOR UPDATE",
                companyUuidColumn,
                companyTableName,
                companyUuidColumn);
    }

    @Override
    public void withLock(String companyUuid, Runnable runnable) {
        transactionTemplate.executeWithoutResult(status -> {
            boolean locked = Boolean.TRUE.equals(
                jdbcTemplate.query(lockSql, Map.of("companyUuid", companyUuid), ResultSet::next));

            if (!locked) {
                throw new IllegalStateException("[org-sync] No company row found for uuid=" + companyUuid);
            }
            runnable.run();
        });
    }
}
