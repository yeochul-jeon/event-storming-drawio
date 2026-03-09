package com.eventstorming.drawio.application.service;

import com.eventstorming.drawio.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DrawioXmlGeneratorTest {

    private final DrawioXmlGenerator generator = new DrawioXmlGenerator();

    @Test
    @DisplayName("하드코딩된 보드 데이터로 draw.io XML을 생성한다")
    void generateDrawioXml() {
        // given
        var board = new EventStormingBoard(
                List.of(
                        new PostIt("postit-1", "주문 생성됨", PostItType.DOMAIN_EVENT, "orange",
                                new Position(100, 200), 160, 80),
                        new PostIt("postit-2", "주문 생성", PostItType.COMMAND, "blue",
                                new Position(100, 50), 160, 80),
                        new PostIt("postit-3", "주문", PostItType.AGGREGATE, "yellow",
                                new Position(100, 350), 160, 80),
                        new PostIt("postit-4", "결제 처리", PostItType.POLICY, "purple",
                                new Position(350, 200), 160, 80),
                        new PostIt("postit-5", "PG사", PostItType.EXTERNAL_SYSTEM, "pink",
                                new Position(350, 350), 160, 80),
                        new PostIt("postit-6", "주문 목록", PostItType.READ_MODEL, "green",
                                new Position(600, 200), 160, 80)
                ),
                List.of(
                        new Connection("conn-1", "postit-2", "postit-1", ""),
                        new Connection("conn-2", "postit-1", "postit-4", ""),
                        new Connection("conn-3", "postit-4", "postit-5", "")
                ),
                1200, 800
        );

        // when
        String xml = generator.generate(board);

        // then
        assertThat(xml).contains("<mxfile>");
        assertThat(xml).contains("event-storming");
        assertThat(xml).contains("주문 생성됨");
        assertThat(xml).contains("주문 생성");
        assertThat(xml).contains("vertex=\"1\"");
        assertThat(xml).contains("edge=\"1\"");
        assertThat(xml).contains("fillColor=#FF8C00"); // DOMAIN_EVENT
        assertThat(xml).contains("fillColor=#4A90D9"); // COMMAND
        assertThat(xml).contains("fillColor=#FFD700"); // AGGREGATE
        assertThat(xml).contains("source=\"postit-2\"");
        assertThat(xml).contains("target=\"postit-1\"");
    }

    @Test
    @DisplayName("빈 보드로도 유효한 XML을 생성한다")
    void generateEmptyBoard() {
        var board = new EventStormingBoard(List.of(), List.of(), 1200, 800);

        String xml = generator.generate(board);

        assertThat(xml).contains("<mxfile>");
        assertThat(xml).contains("<root>");
        assertThat(xml).contains("id=\"0\"");
        assertThat(xml).contains("id=\"1\"");
    }
}
