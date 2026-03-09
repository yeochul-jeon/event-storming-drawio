package com.eventstorming.drawio.adapter.in.web;

import com.eventstorming.drawio.adapter.in.web.dto.AnalysisResponse;
import com.eventstorming.drawio.adapter.in.web.dto.ColorMappingRequest;
import com.eventstorming.drawio.config.DefaultColorMappings;
import com.eventstorming.drawio.domain.model.ColorMapping;
import com.eventstorming.drawio.domain.model.EventStormingBoard;
import com.eventstorming.drawio.domain.model.PostItType;
import com.eventstorming.drawio.domain.port.in.AnalyzeBoardUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BoardController {

    private final AnalyzeBoardUseCase analyzeBoardUseCase;
    private final Map<String, String> xmlStore = new ConcurrentHashMap<>();

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("defaultMappings", DefaultColorMappings.DEFAULTS);
        model.addAttribute("postItTypes", PostItType.values());
        return "index";
    }

    @PostMapping("/api/analyze")
    @ResponseBody
    public AnalysisResponse analyze(
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "mappings", required = false) String mappingsJson
    ) throws IOException {
        List<ColorMapping> customMappings = parseMappings(mappingsJson);

        String mimeType = image.getContentType() != null ? image.getContentType() : "image/png";
        EventStormingBoard board = analyzeBoardUseCase.analyze(image.getBytes(), mimeType, customMappings);
        String xml = analyzeBoardUseCase.generateDrawioXml(board);

        String sessionId = UUID.randomUUID().toString();
        xmlStore.put(sessionId, xml);

        return new AnalysisResponse(sessionId, board, xml, "분석 완료");
    }

    @GetMapping("/api/download/{sessionId}")
    public ResponseEntity<byte[]> download(@PathVariable String sessionId) {
        String xml = xmlStore.get(sessionId);
        if (xml == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=event-storming.drawio")
                .contentType(MediaType.APPLICATION_XML)
                .body(xml.getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping("/mapping")
    public String mappingPage(Model model) {
        model.addAttribute("defaultMappings", DefaultColorMappings.DEFAULTS);
        model.addAttribute("postItTypes", PostItType.values());
        return "mapping";
    }

    private List<ColorMapping> parseMappings(String mappingsJson) {
        if (mappingsJson == null || mappingsJson.isBlank()) {
            return List.of();
        }
        try {
            var objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            var listType = objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, ColorMappingRequest.class);
            List<ColorMappingRequest> requests = objectMapper.readValue(mappingsJson, listType);

            return requests.stream()
                    .map(r -> new ColorMapping(r.colorName(), "", PostItType.valueOf(r.type())))
                    .toList();
        } catch (Exception e) {
            log.warn("색상 매핑 파싱 실패, 기본 매핑 사용: {}", e.getMessage());
            return List.of();
        }
    }
}
