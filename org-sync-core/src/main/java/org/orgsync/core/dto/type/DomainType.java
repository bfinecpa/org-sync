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
    RELATION_MEMBER("멤버 조직 관계");

    private final String title;

    DomainType(String title) {
        this.title = title;
    }
}
