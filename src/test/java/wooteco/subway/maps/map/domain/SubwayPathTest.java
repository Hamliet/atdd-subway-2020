package wooteco.subway.maps.map.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static wooteco.subway.members.member.acceptance.step.MemberAcceptanceStep.내_회원_정보_조회_요청;
import static wooteco.subway.members.member.acceptance.step.MemberAcceptanceStep.로그인_되어_있음;
import static wooteco.subway.members.member.acceptance.step.MemberAcceptanceStep.회원_등록되어_있음;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import wooteco.security.core.TokenResponse;
import wooteco.subway.maps.line.domain.LineStation;

class SubwayPathTest {

    private static final String EMAIL = "email@email.com";
    private static final String PASSWORD = "password";
    private static final Integer AGE = 20;

    @Test
    void calculateTotalFare() {
        List<LineStationEdge> lineStationEdges = Arrays.asList(
            new LineStationEdge(new LineStation(1L, null, 4, 1, 100), 1L),
            new LineStationEdge(new LineStation(2L, 1L, 5, 1, 300), 1L),
            new LineStationEdge(new LineStation(3L, 2L, 15, 1, 500), 1L)
        );
        SubwayPath subwayPath = new SubwayPath(lineStationEdges);

        // 성인, 노선 추가 요금 500, 이용 거리 15km 추가
        assertThat(subwayPath.calculateTotalFare()).isEqualTo(2050);
    }
}