package org.orgsync.core.dto.type;

import org.orgsync.core.Constants;

public enum DomainType {

    COMPANY("회사"),
    COMPANY_GROUP("회사 그룹"),
    INTEGRATION("겸직자") ,
    DEPARTMENT("부서"),
    USER("사용자"),
    USER_INFO("사용자 부가"),
    USERGROUPCODE_USER("사용자 그룹"),
    ORGANIZATION_CODE("조직 코드"),
    RELATION("관계"),
    RELATION_DEPARTMENT("부서 조직 관계"),
    RELATION_MEMBER("멤버 조직 관계"),
    MULTI_LANGUAGE_USER("유저 다국어"),
    MULTI_LANGUAGE_DEPARTMENT("부서 다국어"),
    MULTI_LANGUAGE_ORGANIZATION_CODE("조직 코드 다국어");

    private static final String MULTI_LANGUAGE_FILED_NAME = "multiLanguageMap";
    private final String title;

    DomainType(String title) {
        this.title = title;
    }

    public static DomainType of(DomainType domain, String fieldName) {
        if (MULTI_LANGUAGE_FILED_NAME.equals(fieldName)) {
            return switch (domain) {
                case USER -> MULTI_LANGUAGE_USER;
                case DEPARTMENT -> MULTI_LANGUAGE_DEPARTMENT;
                case ORGANIZATION_CODE -> MULTI_LANGUAGE_ORGANIZATION_CODE;
                default -> throw new IllegalArgumentException(Constants.ORG_SYNC_PREFIX + "Not support multi language domain type: " + domain);
            };
        }
        return domain;
    }
}
