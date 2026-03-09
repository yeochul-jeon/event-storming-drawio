package com.eventstorming.drawio.adapter.in.cli;

import com.eventstorming.drawio.domain.model.ColorMapping;
import com.eventstorming.drawio.domain.model.EventStormingBoard;
import com.eventstorming.drawio.domain.model.PostItType;
import com.eventstorming.drawio.domain.port.in.AnalyzeBoardUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Profile("cli")
@RequiredArgsConstructor
public class CliAdapter implements CommandLineRunner {

    private final AnalyzeBoardUseCase analyzeBoardUseCase;

    @Override
    public void run(String... args) throws Exception {
        String imagePath = getArg(args, "--image");
        String outputPath = getArg(args, "--output");
        String mappingArg = getArg(args, "--mapping");

        if (imagePath == null) {
            System.out.println("사용법: java -jar app.jar --spring.profiles.active=cli --image=<이미지경로> [--output=<출력경로>] [--mapping=\"orange:COMMAND,blue:DOMAIN_EVENT\"]");
            return;
        }

        Path imageFile = Path.of(imagePath);
        if (!Files.exists(imageFile)) {
            System.err.println("이미지 파일을 찾을 수 없습니다: " + imagePath);
            return;
        }

        if (outputPath == null) {
            outputPath = imagePath.replaceAll("\\.[^.]+$", ".drawio");
        }

        String mimeType = detectMimeType(imagePath);
        List<ColorMapping> customMappings = parseMappingArg(mappingArg);

        System.out.println("이미지 분석 시작: " + imagePath);
        byte[] imageData = Files.readAllBytes(imageFile);
        EventStormingBoard board = analyzeBoardUseCase.analyze(imageData, mimeType, customMappings);
        String xml = analyzeBoardUseCase.generateDrawioXml(board);

        Files.writeString(Path.of(outputPath), xml);
        System.out.printf("변환 완료! 포스트잇: %d개, 연결선: %d개%n",
                board.postIts().size(), board.connections().size());
        System.out.println("출력 파일: " + outputPath);
    }

    private String getArg(String[] args, String prefix) {
        for (String arg : args) {
            if (arg.startsWith(prefix + "=")) {
                return arg.substring(prefix.length() + 1);
            }
        }
        return null;
    }

    private String detectMimeType(String path) {
        String lower = path.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".webp")) return "image/webp";
        return "image/png";
    }

    private List<ColorMapping> parseMappingArg(String mappingArg) {
        if (mappingArg == null || mappingArg.isBlank()) {
            return List.of();
        }
        List<ColorMapping> mappings = new ArrayList<>();
        for (String pair : mappingArg.split(",")) {
            String[] parts = pair.trim().split(":");
            if (parts.length == 2) {
                try {
                    mappings.add(new ColorMapping(parts[0].trim(), "", PostItType.valueOf(parts[1].trim())));
                } catch (IllegalArgumentException e) {
                    log.warn("잘못된 매핑: {}", pair);
                }
            }
        }
        return mappings;
    }
}
