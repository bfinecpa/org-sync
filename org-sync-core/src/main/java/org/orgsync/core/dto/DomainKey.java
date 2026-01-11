package org.orgsync.core.dto;

import org.orgsync.core.dto.type.DomainType;

public record DomainKey(DomainType domainType, Long domainId) {

    @Override
    public String toString() {
        return "DomainKey{" +
            "domainType=" + domainType +
            ", domainId=" + domainId +
            '}';
    }
}
