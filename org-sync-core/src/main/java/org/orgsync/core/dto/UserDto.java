package org.orgsync.core.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class UserDto {
private Long id;
private Long companyId;
private String name;
private String employeeNumber;
private String loginId;
private String locale;
private UserStatus status;
private Boolean needOperatorAssignment;
private Map<MultiLanguageType, MultiLanguageDto> multiLanguageMap;
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
private boolean lunarCalendar;
private LocalDate anniversary;
private String address;
private String memo;
private String externalEmail;
private LocalDate joinDate;
private LocalDate recognizedJoinDate;
private String residentRegistrationNumber;
private EmployeeType employeeType;
private Long positionCode;
private Long gradeCode;
private List<Long> userGroupUserList;
private Long integration;


}
