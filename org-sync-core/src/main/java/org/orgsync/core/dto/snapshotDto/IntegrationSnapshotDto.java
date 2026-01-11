package org.orgsync.core.dto.snapshotDto;

import java.util.List;

public class IntegrationSnapshotDto {


    private Long id;

    private List<Long> userIdList;


    public Long getId() {
        return id;
    }

    public List<Long> getUserIdList() {
        return userIdList;
    }
}
