package com.eventstorming.drawio.adapter.out.ai;

import com.eventstorming.drawio.domain.model.ColorMapping;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClaudePromptBuilder {

    public String buildSystemPrompt(List<ColorMapping> colorMappings) {
        String mappingRules = colorMappings.stream()
                .map(m -> "- %s(%s) -> %s".formatted(m.colorName(), m.hexCode(), m.type().name()))
                .collect(Collectors.joining("\n"));

        return """
                당신은 이벤트 스토밍 보드 이미지를 분석하는 전문가입니다.
                이미지에서 포스트잇(sticky note)을 감지하고, 각 포스트잇의 색상, 텍스트, 위치를 추출해야 합니다.

                ## 색상-유형 매핑 규칙
                %s

                ## 출력 규칙
                1. 각 포스트잇의 색상을 감지하여 위 매핑 규칙에 따라 type을 결정하세요.
                2. 확실하지 않은 색상은 type을 "UNKNOWN"으로 설정하세요.
                3. 좌표는 1200x800 캔버스 기준으로 정규화하세요.
                4. 포스트잇 간의 화살표나 연결선도 감지하세요.
                5. 텍스트가 읽기 어려운 경우 최선의 추측을 하되, 확실하지 않으면 "???"로 표시하세요.

                ## 반드시 아래 JSON 형식으로만 응답하세요 (다른 텍스트 없이):
                ```json
                {
                  "postIts": [
                    {
                      "id": "postit-1",
                      "text": "주문 생성됨",
                      "type": "DOMAIN_EVENT",
                      "detectedColor": "orange",
                      "x": 100,
                      "y": 200,
                      "width": 160,
                      "height": 80
                    }
                  ],
                  "connections": [
                    {
                      "id": "conn-1",
                      "sourceId": "postit-1",
                      "targetId": "postit-2",
                      "label": ""
                    }
                  ],
                  "boardWidth": 1200,
                  "boardHeight": 800
                }
                ```
                """.formatted(mappingRules);
    }

    public String buildUserPrompt() {
        return "이 이벤트 스토밍 보드 이미지를 분석해주세요. 모든 포스트잇의 색상, 텍스트, 위치와 포스트잇 간의 연결선을 JSON으로 추출해주세요.";
    }
}
