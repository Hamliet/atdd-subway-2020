package wooteco.subway.maps.map.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import wooteco.subway.maps.line.domain.LineStation;

class SubwayPathTest {

    @Test
    void calculateTotalFare() {
        List<LineStationEdge> lineStationEdges = Arrays.asList(
            new LineStationEdge(new LineStation(1L, null, 4, 1), 1L),
            new LineStationEdge(new LineStation(2L, 1L, 5, 1), 1L),
            new LineStationEdge(new LineStation(3L, 2L, 15, 1), 1L)
        );
        SubwayPath subwayPath = new SubwayPath(lineStationEdges);

        // 성인, 노선 추가 요금 0, 이용거리 10km 추가
        assertThat(subwayPath.calculateTotalFare()).isEqualTo(1550);

    }
}