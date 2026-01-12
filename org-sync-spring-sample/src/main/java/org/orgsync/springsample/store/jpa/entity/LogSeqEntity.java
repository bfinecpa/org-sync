package org.orgsync.springsample.store.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "org_sync_log_seq")
public class LogSeqEntity {

    @Id
    private Long companyId;

    @Column(unique = true)
    private String companyUuid;

    private Long logSeq;

    protected LogSeqEntity() {
    }

    public LogSeqEntity(Long companyId, String companyUuid, Long logSeq) {
        this.companyId = companyId;
        this.companyUuid = companyUuid;
        this.logSeq = logSeq;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public String getCompanyUuid() {
        return companyUuid;
    }

    public void setCompanyUuid(String companyUuid) {
        this.companyUuid = companyUuid;
    }

    public Long getLogSeq() {
        return logSeq;
    }

    public void setLogSeq(Long logSeq) {
        this.logSeq = logSeq;
    }
}
