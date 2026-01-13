package org.orgsync.core.dto;

import java.util.Objects;
import org.orgsync.core.dto.type.DomainType;

public record DomainKey(DomainType domainType, Long domainId) {

    @Override
    public String toString() {
        return "DomainKey{" +
            "domainType=" + domainType +
            ", domainId=" + domainId +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DomainKey domainKey = (DomainKey) o;
        return Objects.equals(domainId, domainKey.domainId) && domainType == domainKey.domainType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(domainType, domainId);
    }
}
