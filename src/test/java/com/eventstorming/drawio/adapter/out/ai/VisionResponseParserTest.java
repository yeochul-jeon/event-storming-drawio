package com.eventstorming.drawio.adapter.out.ai;

import com.eventstorming.drawio.domain.model.EventStormingBoard;
import com.eventstorming.drawio.domain.model.PostItType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VisionResponseParserTest {

    private final VisionResponseParser parser = new VisionResponseParser(new ObjectMapper());

    @Test
    @DisplayName("JSON 코드 블록이 포함된 응답을 파싱한다")
    void parseJsonCodeBlock() {
        String response = """
                분석 결과입니다:
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
                """;

        EventStormingBoard board = parser.parse(response);

        assertThat(board.postIts()).hasSize(1);
        assertThat(board.postIts().get(0).text()).isEqualTo("주문 생성됨");
        assertThat(board.postIts().get(0).type()).isEqualTo(PostItType.DOMAIN_EVENT);
        assertThat(board.connections()).hasSize(1);
    }

    @Test
    @DisplayName("순수 JSON 응답을 파싱한다")
    void parsePureJson() {
        String response = """
                {
                  "postIts": [
                    {
                      "id": "postit-1",
                      "text": "결제 완료",
                      "type": "DOMAIN_EVENT",
                      "detectedColor": "orange",
                      "x": 300,
                      "y": 100
                    }
                  ],
                  "connections": [],
                  "boardWidth": 1200,
                  "boardHeight": 800
                }
                """;

        EventStormingBoard board = parser.parse(response);

        assertThat(board.postIts()).hasSize(1);
        assertThat(board.postIts().get(0).width()).isEqualTo(160); // 기본값
        assertThat(board.connections()).isEmpty();
    }

    @Test
    @DisplayName("알 수 없는 타입은 UNKNOWN으로 처리한다")
    void parseUnknownType() {
        String response = """
                {
                  "postIts": [
                    {
                      "id": "postit-1",
                      "text": "테스트",
                      "type": "INVALID_TYPE",
                      "detectedColor": "red",
                      "x": 0,
                      "y": 0
                    }
                  ],
                  "connections": [],
                  "boardWidth": 1200,
                  "boardHeight": 800
                }
                """;

        EventStormingBoard board = parser.parse(response);

        assertThat(board.postIts().get(0).type()).isEqualTo(PostItType.UNKNOWN);
    }
}
