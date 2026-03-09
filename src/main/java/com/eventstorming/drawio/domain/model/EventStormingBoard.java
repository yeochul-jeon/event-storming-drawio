package com.eventstorming.drawio.domain.model;

import java.util.List;

public record EventStormingBoard(
        List<PostIt> postIts,
        List<Connection> connections,
        double boardWidth,
        double boardHeight
) {
}
