package com.eventstorming.drawio.adapter.out.ai;

import com.eventstorming.drawio.domain.model.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClaudeResponseParser {

    private final ObjectMapper objectMapper;

    public EventStormingBoard parse(String responseContent) {
        try {
            String json = extractJson(responseContent);
            JsonNode root = objectMapper.readTree(json);

            List<PostIt> postIts = parsePostIts(root.path("postIts"));
            List<Connection> connections = parseConnections(root.path("connections"));
            double boardWidth = root.path("boardWidth").asDouble(1200);
            double boardHeight = root.path("boardHeight").asDouble(800);

            return new EventStormingBoard(postIts, connections, boardWidth, boardHeight);
        } catch (Exception e) {
            log.error("Claude 응답 파싱 실패: {}", responseContent, e);
            throw new RuntimeException("AI 응답을 파싱할 수 없습니다.", e);
        }
    }

    private String extractJson(String content) {
        // ```json ... ``` 블록 추출
        int start = content.indexOf("```json");
        if (start != -1) {
            start = content.indexOf("\n", start) + 1;
            int end = content.indexOf("```", start);
            return content.substring(start, end).trim();
        }
        // ```  ... ``` 블록 추출
        start = content.indexOf("```");
        if (start != -1) {
            start = content.indexOf("\n", start) + 1;
            int end = content.indexOf("```", start);
            return content.substring(start, end).trim();
        }
        // JSON 직접 반환
        int jsonStart = content.indexOf("{");
        int jsonEnd = content.lastIndexOf("}");
        if (jsonStart != -1 && jsonEnd != -1) {
            return content.substring(jsonStart, jsonEnd + 1);
        }
        return content;
    }

    private List<PostIt> parsePostIts(JsonNode postItsNode) {
        List<PostIt> postIts = new ArrayList<>();
        if (postItsNode.isMissingNode() || !postItsNode.isArray()) {
            return postIts;
        }

        for (JsonNode node : postItsNode) {
            var postIt = new PostIt(
                    node.path("id").asText(),
                    node.path("text").asText(),
                    parsePostItType(node.path("type").asText()),
                    node.path("detectedColor").asText(),
                    new Position(node.path("x").asDouble(), node.path("y").asDouble()),
                    node.path("width").asDouble(160),
                    node.path("height").asDouble(80)
            );
            postIts.add(postIt);
        }
        return postIts;
    }

    private PostItType parsePostItType(String type) {
        try {
            return PostItType.valueOf(type);
        } catch (IllegalArgumentException e) {
            log.warn("알 수 없는 PostItType: {}", type);
            return PostItType.UNKNOWN;
        }
    }

    private List<Connection> parseConnections(JsonNode connectionsNode) {
        List<Connection> connections = new ArrayList<>();
        if (connectionsNode.isMissingNode() || !connectionsNode.isArray()) {
            return connections;
        }

        for (JsonNode node : connectionsNode) {
            var connection = new Connection(
                    node.path("id").asText(),
                    node.path("sourceId").asText(),
                    node.path("targetId").asText(),
                    node.path("label").asText("")
            );
            connections.add(connection);
        }
        return connections;
    }
}
