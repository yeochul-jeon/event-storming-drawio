package com.eventstorming.drawio.domain.port.in;

import com.eventstorming.drawio.domain.model.ColorMapping;
import com.eventstorming.drawio.domain.model.EventStormingBoard;

import java.util.List;

public interface AnalyzeBoardUseCase {

    EventStormingBoard analyze(byte[] imageData, String mimeType, List<ColorMapping> customMappings);

    String generateDrawioXml(EventStormingBoard board);
}
