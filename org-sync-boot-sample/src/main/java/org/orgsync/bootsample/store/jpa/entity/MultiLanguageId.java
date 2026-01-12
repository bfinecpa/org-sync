package org.orgsync.bootsample.store.jpa.entity;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.orgsync.core.dto.type.MultiLanguageType;
import org.orgsync.core.dto.type.TargetDomain;

@Embeddable
public class MultiLanguageId implements Serializable {

    private Long id;

    @Enumerated(EnumType.STRING)
    private TargetDomain targetDomain;

    @Enumerated(EnumType.STRING)
    private MultiLanguageType multiLanguageType;

    protected MultiLanguageId() {
    }

    public MultiLanguageId(Long id, TargetDomain targetDomain, MultiLanguageType multiLanguageType) {
        this.id = id;
        this.targetDomain = targetDomain;
        this.multiLanguageType = multiLanguageType;
    }

    public Long getId() {
        return id;
    }

    public TargetDomain getTargetDomain() {
        return targetDomain;
    }

    public MultiLanguageType getMultiLanguageType() {
        return multiLanguageType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MultiLanguageId)) {
            return false;
        }
        MultiLanguageId that = (MultiLanguageId) o;
        return Objects.equals(id, that.id)
            && targetDomain == that.targetDomain
            && multiLanguageType == that.multiLanguageType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, targetDomain, multiLanguageType);
    }
}
