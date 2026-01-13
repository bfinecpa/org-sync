package org.orgsync.core.dto.deltaDto;

import java.time.LocalDate;
import org.orgsync.core.Constants;
import org.orgsync.core.dto.LogInfoDto;
import org.orgsync.core.dto.type.EmployeeType;
import org.orgsync.core.dto.type.UserStatus;

public class UserDeltaDto implements Settable {
    private Long id;
    private String name;
    private String employeeNumber;
    private String loginId;
    private String locale;
    private UserStatus status;
    private Boolean needOperatorAssignment;
    private String multiLanguageMap;
    private String directTel;
    private String mobileNo;
    private String mobileSearch;
    private String repTel;
    private String fax;
    private String selfInfo;
    private String job;
    private String location;
    private String homePage;
    private String messenger;
    private LocalDate birthday;
    private Boolean lunarCalendar;
    private LocalDate anniversary;
    private String address;
    private String memo;
    private String externalEmail;
    private LocalDate joinDate;
    private LocalDate recognizedJoinDate;
    private String residentRegistrationNumber;
    private EmployeeType employeeType;
    private Long positionCodeId;
    private Long gradeCodeId;
    private String userGroupUserList;

    public UserDeltaDto(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
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

    public String getMultiLanguageMap() {
        return multiLanguageMap;
    }

    public String getDirectTel() {
        return directTel;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public String getMobileSearch() {
        return mobileSearch;
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

    public Boolean getLunarCalendar() {
        return Boolean.TRUE.equals(lunarCalendar);
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

    public String getUserGroupUserList() {
        return userGroupUserList;
    }


    @Override
    public void set(LogInfoDto logInfoDto) {
        if (logInfoDto == null) {
            throw new IllegalArgumentException(Constants.ORG_SYNC_PREFIX + "logInfoDto is null in UserDto");
        }

        Object updatedValue = logInfoDto.updatedValue();
        if (updatedValue == null) {
            return;
        }

        switch (logInfoDto.fieldName()) {
            case "id" -> setId(Long.valueOf(updatedValue.toString()));
            case "name" -> setName(updatedValue.toString());
            case "employeeNumber" -> setEmployeeNumber(updatedValue.toString());
            case "loginId" -> setLoginId(updatedValue.toString());
            case "locale" -> setLocale(updatedValue.toString());
            case "status" -> setStatus(UserStatus.valueOf(updatedValue.toString()));
            case "needOperatorAssignment" -> setNeedOperatorAssignment(updatedValue.toString());
            case "multiLanguageMap" -> setMultiLanguageMap(updatedValue.toString());
            case "directTel" -> setDirectTel(updatedValue.toString());
            case "mobileNo" -> setMobileNo(updatedValue.toString());
            case "mobileSearch" -> setMobileSearch(updatedValue.toString());
            case "repTel" -> setRepTel(updatedValue.toString());
            case "fax" -> setFax(updatedValue.toString());
            case "selfInfo" -> setSelfInfo(updatedValue.toString());
            case "job" -> setJob(updatedValue.toString());
            case "location" -> setLocation(updatedValue.toString());
            case "homePage" -> setHomePage(updatedValue.toString());
            case "messenger" -> setMessenger(updatedValue.toString());
            case "birthday" -> setBirthday(LocalDate.parse(updatedValue.toString()));
            case "lunarCalendar" -> setLunarCalendar(updatedValue.toString());
            case "anniversary" -> setAnniversary(LocalDate.parse(updatedValue.toString()));
            case "address" -> setAddress(updatedValue.toString());
            case "memo" -> setMemo(updatedValue.toString());
            case "externalEmail" -> setExternalEmail(updatedValue.toString());
            case "joinDate" -> setJoinDate(LocalDate.parse(updatedValue.toString()));
            case "recognizedJoinDate" -> setRecognizedJoinDate(LocalDate.parse(updatedValue.toString()));
            case "residentRegistrationNumber" -> setResidentRegistrationNumber(updatedValue.toString());
            case "employeeType" -> setEmployeeType(EmployeeType.valueOf(updatedValue.toString()));
            case "positionCode" -> setPositionCodeId(updatedValue.toString());
            case "gradeCode" -> setGradeCodeId(updatedValue.toString());
            case "userGroupUserList" -> setUserGroupUserList(updatedValue.toString());
        }
    }

    private void setId(Long id) {
        this.id = id;
    }


    private void setName(String name) {
        this.name = name;
    }

    private void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    private void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    private void setLocale(String locale) {
        this.locale = locale;
    }

    private void setStatus(UserStatus status) {
        this.status = status;
    }

    private void setNeedOperatorAssignment(String needOperatorAssignment) {
        if (needOperatorAssignment == null || needOperatorAssignment.isEmpty()) {
            this.needOperatorAssignment = false;
            return;
        }
        this.needOperatorAssignment = Boolean.parseBoolean(needOperatorAssignment);
    }

    private void setMultiLanguageMap(String multiLanguageMap) {
        this.multiLanguageMap = multiLanguageMap;
    }

    private void setDirectTel(String directTel) {
        this.directTel = directTel;
    }

    private void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    private void setMobileSearch(String mobileSearch) {
        this.mobileSearch = mobileSearch;
    }

    private void setRepTel(String repTel) {
        this.repTel = repTel;
    }

    private void setFax(String fax) {
        this.fax = fax;
    }

    private void setSelfInfo(String selfInfo) {
        this.selfInfo = selfInfo;
    }

    private void setJob(String job) {
        this.job = job;
    }

    private void setLocation(String location) {
        this.location = location;
    }

    private void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    private void setMessenger(String messenger) {
        this.messenger = messenger;
    }

    private void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    private void setLunarCalendar(String lunarCalendar) {
        if (lunarCalendar == null || lunarCalendar.isEmpty()) {
            this.lunarCalendar = false;
        }else {
            this.lunarCalendar = Boolean.parseBoolean(lunarCalendar);
        }
    }

    private void setAnniversary(LocalDate anniversary) {
        this.anniversary = anniversary;
    }

    private void setAddress(String address) {
        this.address = address;
    }

    private void setMemo(String memo) {
        this.memo = memo;
    }

    private void setExternalEmail(String externalEmail) {
        this.externalEmail = externalEmail;
    }

    private void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    private void setRecognizedJoinDate(LocalDate recognizedJoinDate) {
        this.recognizedJoinDate = recognizedJoinDate;
    }

    private void setResidentRegistrationNumber(String residentRegistrationNumber) {
        this.residentRegistrationNumber = residentRegistrationNumber;
    }

    private void setEmployeeType(EmployeeType employeeType) {
        this.employeeType = employeeType;
    }

    private void setPositionCodeId(String positionCodeId) {
        if (positionCodeId == null || positionCodeId.isEmpty()) {
            this.positionCodeId = null;
        }else {
            this.positionCodeId = Long.parseLong(positionCodeId);

        }
    }

    private void setGradeCodeId(String gradeCodeId) {
        if (gradeCodeId == null || gradeCodeId.isEmpty()) {
            this.gradeCodeId = null;
        }else {
            this.gradeCodeId = Long.parseLong(gradeCodeId);

        }
    }

    private void setUserGroupUserList(String userGroupUserList) {
        this.userGroupUserList = userGroupUserList;
    }
}
