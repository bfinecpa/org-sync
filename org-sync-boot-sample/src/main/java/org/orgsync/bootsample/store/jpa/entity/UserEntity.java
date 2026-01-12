package org.orgsync.bootsample.store.jpa.entity;

import java.time.LocalDate;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.orgsync.core.dto.type.EmployeeType;
import org.orgsync.core.dto.type.UserStatus;

@Entity
@Table(name = "org_sync_user")
public class UserEntity {

    @Id
    private Long id;

    private Long companyId;

    private String name;

    private String employeeNumber;

    private String loginId;

    private String locale;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private Boolean needOperatorAssignment;

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

    private boolean lunarCalendar;

    private LocalDate anniversary;

    private String address;

    private String memo;

    private String externalEmail;

    private LocalDate joinDate;

    private LocalDate recognizedJoinDate;

    private String residentRegistrationNumber;

    @Enumerated(EnumType.STRING)
    private EmployeeType employeeType;

    private Long positionCodeId;

    private Long gradeCodeId;

    private Long integrationId;

    protected UserEntity() {
    }

    public UserEntity(Long id, Long companyId, String name, String employeeNumber, String loginId, String locale,
                      UserStatus status, Boolean needOperatorAssignment, String directTel, String mobileNo,
                      String repTel, String fax, String selfInfo, String job, String location, String homePage,
                      String messenger, LocalDate birthday, boolean lunarCalendar, LocalDate anniversary, String address,
                      String memo, String externalEmail, LocalDate joinDate, LocalDate recognizedJoinDate,
                      String residentRegistrationNumber, EmployeeType employeeType, Long positionCodeId,
                      Long gradeCodeId, Long integrationId) {
        this.id = id;
        this.companyId = companyId;
        this.name = name;
        this.employeeNumber = employeeNumber;
        this.loginId = loginId;
        this.locale = locale;
        this.status = status;
        this.needOperatorAssignment = needOperatorAssignment;
        this.directTel = directTel;
        this.mobileNo = mobileNo;
        this.repTel = repTel;
        this.fax = fax;
        this.selfInfo = selfInfo;
        this.job = job;
        this.location = location;
        this.homePage = homePage;
        this.messenger = messenger;
        this.birthday = birthday;
        this.lunarCalendar = lunarCalendar;
        this.anniversary = anniversary;
        this.address = address;
        this.memo = memo;
        this.externalEmail = externalEmail;
        this.joinDate = joinDate;
        this.recognizedJoinDate = recognizedJoinDate;
        this.residentRegistrationNumber = residentRegistrationNumber;
        this.employeeType = employeeType;
        this.positionCodeId = positionCodeId;
        this.gradeCodeId = gradeCodeId;
        this.integrationId = integrationId;
    }

    public Long getId() {
        return id;
    }

    public Long getCompanyId() {
        return companyId;
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

    public Boolean getNeedOperatorAssignment() {
        return needOperatorAssignment;
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

    public boolean isLunarCalendar() {
        return lunarCalendar;
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

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public LocalDate getRecognizedJoinDate() {
        return recognizedJoinDate;
    }

    public String getResidentRegistrationNumber() {
        return residentRegistrationNumber;
    }

    public EmployeeType getEmployeeType() {
        return employeeType;
    }

    public Long getPositionCodeId() {
        return positionCodeId;
    }

    public Long getGradeCodeId() {
        return gradeCodeId;
    }

    public Long getIntegrationId() {
        return integrationId;
    }

    public void setIntegrationId(Long integrationId) {
        this.integrationId = integrationId;
    }
}
