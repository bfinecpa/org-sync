package org.orgsync.springsample.store.jpa.entity;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Embeddable;

@Embeddable
public class UserGroupUserId implements Serializable {

    private Long userId;
    private Long userGroupId;

    protected UserGroupUserId() {
    }

    public UserGroupUserId(Long userId, Long userGroupId) {
        this.userId = userId;
        this.userGroupId = userGroupId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getUserGroupId() {
        return userGroupId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserGroupUserId)) {
            return false;
        }
        UserGroupUserId that = (UserGroupUserId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(userGroupId, that.userGroupId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userGroupId);
    }
}
