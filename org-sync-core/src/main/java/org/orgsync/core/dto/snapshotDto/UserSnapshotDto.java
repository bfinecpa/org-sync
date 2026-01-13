package org.orgsync.core.dto.snapshotDto;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.orgsync.core.dto.domainDto.MultiLanguageDto;
import org.orgsync.core.dto.domainDto.UserDto;
import org.orgsync.core.dto.type.EmployeeType;
import org.orgsync.core.dto.type.MultiLanguageType;
import org.orgsync.core.dto.type.TargetDomain;
import org.orgsync.core.dto.type.UserStatus;

public record UserSnapshotDto(
    Long userId,
    ZonedDateTime createdAt,
    ZonedDateTime updatedAt,
    Long repId,
    String name,
    String employeeNumber,
    String loginId,
    String locale,
    UserStatus status,
    ZonedDateTime deletedAt,
    ZonedDateTime dormantAt,
    Map<MultiLanguageType, String> multiLanguageMap,
    List<Long> departmentIds,
    String position,
    Long positionId,
    String grade,
    Long gradeId,
    List<UserGroupSnapshotDto> userGroupList,
    String directTel,
    String mobileNo,
    String repTel,
    String fax,
    String selfInfo,
    String job,
    String location,
    String homePage,
    String messenger,
    LocalDate birthday,
    boolean lunarBirthday,
    LocalDate anniversary,
    String address,
    String memo,
    String externalEmail,
    LocalDate expiredDate,
    LocalDate joinDate,
    LocalDate resignationDate,
    String residentRegistrationNumber,
    String employeeType,
    String profileImagePath,
    Boolean needOperatorAssignment
) {

    public UserDto toUserDto(Long companyId) {
        EmployeeType resolvedEmployeeType = employeeType == null ? null : EmployeeType.valueOf(employeeType);
        return new UserDto(
            userId,
            companyId,
            name,
            employeeNumber,
            loginId,
            locale,
            status,
            needOperatorAssignment,
            directTel,
            mobileNo,
            repTel,
            fax,
            selfInfo,
            job,
            location,
            homePage,
            messenger,
            birthday,
            lunarBirthday,
            anniversary,
            address,
            memo,
            externalEmail,
            joinDate,
            null,
            residentRegistrationNumber,
            resolvedEmployeeType,
            positionId,
            gradeId
        );
    }

    public List<MultiLanguageDto> toMultiLanguageDtos() {
        List<MultiLanguageDto> multiLanguageDtos = new ArrayList<>();
        for (Entry<MultiLanguageType, String> entry : multiLanguageMap.entrySet()) {
            multiLanguageDtos.add(new MultiLanguageDto(this.userId, TargetDomain.USER, entry.getKey(), entry.getValue()));
        }
        return multiLanguageDtos;
    }
}
