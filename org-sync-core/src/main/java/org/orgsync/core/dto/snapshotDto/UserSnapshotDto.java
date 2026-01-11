package org.orgsync.core.dto.snapshotDto;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.orgsync.core.dto.domainDto.MultiLanguageDto;
import org.orgsync.core.dto.type.MultiLanguageType;
import org.orgsync.core.dto.type.TargetDomain;
import org.orgsync.core.dto.domainDto.UserDto;
import org.orgsync.core.dto.type.EmployeeType;
import org.orgsync.core.dto.type.UserStatus;

public class UserSnapshotDto {
    private Long userId;

    private ZonedDateTime createdAt;

    private ZonedDateTime updatedAt;

    private Long repId;

    private String name;

    private String employeeNumber;

    private String loginId;

    private String locale;

    private UserStatus status;

    private ZonedDateTime deletedAt;

    private ZonedDateTime dormantAt;

    private Map<MultiLanguageType, String> multiLanguageMap;

    private List<Long> departmentIds;

    private String position;

    private Long positionId;

    private String grade;

    private Long gradeId;

    private List<UserGroupSnapshotDto> userGroupList; // 이때는 UserGroupUser의 id이다.

    private String directTel;

    private String mobileNo;

    private String repTel;

    private String fax;

    private String selfInfo;

    private String job;

    private String location;

    private String homePage;

    private String messenger;

    private LocalDate birthday;

    private boolean lunarBirthday;

    private LocalDate anniversary;

    private String address;

    private String memo;

    private String externalEmail;

    private LocalDate expiredDate;

    private LocalDate joinDate;

    private LocalDate resignationDate;

    private String residentRegistrationNumber;

    private String employeeType;

    private String profileImagePath;

    private Boolean needOperatorAssignment;


    public Long getUserId() {
        return userId;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getRepId() {
        return repId;
    }

    public String getName() {
        return name;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getLocale() {
        return locale;
    }

    public UserStatus getStatus() {
        return status;
    }

    public ZonedDateTime getDeletedAt() {
        return deletedAt;
    }

    public ZonedDateTime getDormantAt() {
        return dormantAt;
    }

    public Map<MultiLanguageType, String> getMultiLanguageMap() {
        return multiLanguageMap;
    }

    public List<Long> getDepartmentIds() {
        return departmentIds;
    }

    public String getPosition() {
        return position;
    }

    public Long getPositionId() {
        return positionId;
    }

    public String getGrade() {
        return grade;
    }

    public Long getGradeId() {
        return gradeId;
    }

    public List<UserGroupSnapshotDto> getUserGroupList() {
        return userGroupList;
    }

    public String getDirectTel() {
        return directTel;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public String getRepTel() {
        return repTel;
    }

    public String getFax() {
        return fax;
    }

    public String getSelfInfo() {
        return selfInfo;
    }

    public String getJob() {
        return job;
    }

    public String getLocation() {
        return location;
    }

    public String getHomePage() {
        return homePage;
    }

    public String getMessenger() {
        return messenger;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public boolean isLunarBirthday() {
        return lunarBirthday;
    }

    public LocalDate getAnniversary() {
        return anniversary;
    }

    public String getAddress() {
        return address;
    }

    public String getMemo() {
        return memo;
    }

    public String getExternalEmail() {
        return externalEmail;
    }

    public LocalDate getExpiredDate() {
        return expiredDate;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public LocalDate getResignationDate() {
        return resignationDate;
    }

    public String getResidentRegistrationNumber() {
        return residentRegistrationNumber;
    }

    public String getEmployeeType() {
        return employeeType;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public Boolean getNeedOperatorAssignment() {
        return needOperatorAssignment;
    }

    public UserDto toUserDto() {
        EmployeeType resolvedEmployeeType = employeeType == null ? null : EmployeeType.valueOf(employeeType);
        return new UserDto(
            userId,
            null,
            name,
            employeeNumber,
            loginId,
            locale,
            status,
            needOperatorAssignment,
            directTel,
            mobileNo,
            null,
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
