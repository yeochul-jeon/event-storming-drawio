package com.eventstorming.drawio.application.service;

import com.eventstorming.drawio.domain.model.ColorMapping;
import com.eventstorming.drawio.domain.model.EventStormingBoard;
import com.eventstorming.drawio.domain.port.in.AnalyzeBoardUseCase;
import com.eventstorming.drawio.domain.port.out.ImageAnalysisPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardAnalysisService implements AnalyzeBoardUseCase {

    private final ImageAnalysisPort imageAnalysisPort;
    private final ColorMappingService colorMappingService;
    private final DrawioXmlGenerator drawioXmlGenerator;

    @Override
    public EventStormingBoard analyze(byte[] imageData, String mimeType, List<ColorMapping> customMappings) {
        List<ColorMapping> mergedMappings = colorMappingService.merge(customMappings);
        log.info("이미지 분석 시작 - 적용된 색상 매핑: {}", mergedMappings.size());

        EventStormingBoard board = imageAnalysisPort.analyzeImage(imageData, mimeType, mergedMappings);
        log.info("이미지 분석 완료 - 포스트잇: {}개, 연결선: {}개",
                board.postIts().size(), board.connections().size());

        return board;
    }

    @Override
    public String generateDrawioXml(EventStormingBoard board) {
        return drawioXmlGenerator.generate(board);
    }
}
