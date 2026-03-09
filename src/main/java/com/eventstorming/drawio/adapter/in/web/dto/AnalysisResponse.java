package com.eventstorming.drawio.adapter.in.web.dto;

import com.eventstorming.drawio.domain.model.EventStormingBoard;

public record AnalysisResponse(
        String sessionId,
        EventStormingBoard board,
        String drawioXml,
        String message
) {
}
