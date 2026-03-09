package com.eventstorming.drawio.adapter.out.ai;

import com.eventstorming.drawio.domain.model.ColorMapping;
import com.eventstorming.drawio.domain.model.EventStormingBoard;
import com.eventstorming.drawio.domain.port.out.ImageAnalysisPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClaudeVisionAdapter implements ImageAnalysisPort {

    private final ChatClient.Builder chatClientBuilder;
    private final ClaudePromptBuilder promptBuilder;
    private final ClaudeResponseParser responseParser;

    @Override
    public EventStormingBoard analyzeImage(byte[] imageData, String mimeType, List<ColorMapping> colorMappings) {
        log.info("Claude Vision API 호출 시작 - 이미지 크기: {} bytes, MIME: {}", imageData.length, mimeType);

        String systemPrompt = promptBuilder.buildSystemPrompt(colorMappings);
        String userPrompt = promptBuilder.buildUserPrompt();

        String response = chatClientBuilder.build()
                .prompt()
                .system(systemPrompt)
                .user(u -> u.text(userPrompt)
                        .media(MimeType.valueOf(mimeType), new ByteArrayResource(imageData)))
                .call()
                .content();

        log.debug("Claude Vision API 응답: {}", response);
        return responseParser.parse(response);
    }
}
