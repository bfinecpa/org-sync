package org.orgsync.bootsample.store.jpa.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "org_sync_user_group_user")
public class UserGroupUserEntity {

    @EmbeddedId
    private UserGroupUserId id;

    protected UserGroupUserEntity() {
    }

    public UserGroupUserEntity(UserGroupUserId id) {
        this.id = id;
    }

    public UserGroupUserId getId() {
        return id;
    }
}
