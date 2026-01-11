package org.orgsync.core.dto.type;

public enum LogType {

    CREATE("생성"), UPDATE("수정"), DELETE("삭제");


    private final String title;

    LogType(String title) {
        this.title = title;
    }
}
