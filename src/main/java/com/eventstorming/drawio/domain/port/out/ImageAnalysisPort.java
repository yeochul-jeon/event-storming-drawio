package com.eventstorming.drawio.domain.port.out;

import com.eventstorming.drawio.domain.model.ColorMapping;
import com.eventstorming.drawio.domain.model.EventStormingBoard;

import java.util.List;

public interface ImageAnalysisPort {

    EventStormingBoard analyzeImage(byte[] imageData, String mimeType, List<ColorMapping> colorMappings);
}
