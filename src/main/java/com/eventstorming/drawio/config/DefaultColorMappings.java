package com.eventstorming.drawio.config;

import com.eventstorming.drawio.domain.model.ColorMapping;
import com.eventstorming.drawio.domain.model.PostItType;

import java.util.List;

public final class DefaultColorMappings {

    private DefaultColorMappings() {
    }

    public static final List<ColorMapping> DEFAULTS = List.of(
            new ColorMapping("orange", "#FF8C00", PostItType.DOMAIN_EVENT),
            new ColorMapping("blue", "#4A90D9", PostItType.COMMAND),
            new ColorMapping("yellow", "#FFD700", PostItType.AGGREGATE),
            new ColorMapping("purple", "#9B59B6", PostItType.POLICY),
            new ColorMapping("pink", "#FF69B4", PostItType.EXTERNAL_SYSTEM),
            new ColorMapping("green", "#2ECC71", PostItType.READ_MODEL)
    );
}
