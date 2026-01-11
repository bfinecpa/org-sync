package org.orgsync.core.dto.domainDto;

/**
 * id가 없는 이유는 현재 프로비저닝 스펙으로 id가 없이 내려갑니다.
 */
public class UserGroupUserDto {

    private Long userId;
    private Long userGroupId;

    public UserGroupUserDto(Long userId, Long userGroupId) {
        this.userId = userId;
        this.userGroupId = userGroupId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getUserGroupId() {
        return userGroupId;
    }
}
