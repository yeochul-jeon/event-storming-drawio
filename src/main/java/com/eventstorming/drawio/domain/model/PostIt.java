package com.eventstorming.drawio.domain.model;

public record PostIt(
        String id,
        String text,
        PostItType type,
        String detectedColor,
        Position position,
        double width,
        double height
) {
    public PostIt(String id, String text, PostItType type, String detectedColor, Position position) {
        this(id, text, type, detectedColor, position, 160, 80);
    }
}
