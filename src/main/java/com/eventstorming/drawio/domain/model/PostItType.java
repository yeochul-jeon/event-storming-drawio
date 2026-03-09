package com.eventstorming.drawio.domain.model;

public enum PostItType {
    DOMAIN_EVENT("도메인 이벤트", "#FF8C00", "#FFFFFF"),
    COMMAND("커맨드", "#4A90D9", "#FFFFFF"),
    AGGREGATE("애그리거트", "#FFD700", "#333333"),
    POLICY("정책", "#9B59B6", "#FFFFFF"),
    EXTERNAL_SYSTEM("외부 시스템", "#FF69B4", "#FFFFFF"),
    READ_MODEL("읽기 모델", "#2ECC71", "#FFFFFF"),
    UNKNOWN("알 수 없음", "#CCCCCC", "#333333");

    private final String displayName;
    private final String fillColor;
    private final String fontColor;

    PostItType(String displayName, String fillColor, String fontColor) {
        this.displayName = displayName;
        this.fillColor = fillColor;
        this.fontColor = fontColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFillColor() {
        return fillColor;
    }

    public String getFontColor() {
        return fontColor;
    }
}
