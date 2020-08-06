package wooteco.subway.maps.map.domain;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;
import wooteco.security.core.context.SecurityContextHolder;
import wooteco.subway.members.member.domain.LoginMember;

public class SubwayPath {

    private List<LineStationEdge> lineStationEdges;

    public SubwayPath(List<LineStationEdge> lineStationEdges) {
        this.lineStationEdges = lineStationEdges;
    }

    public List<LineStationEdge> getLineStationEdges() {
        return lineStationEdges;
    }

    public List<Long> extractStationId() {
        List<Long> stationIds = Lists
            .newArrayList(lineStationEdges.get(0).getLineStation().getPreStationId());
        stationIds.addAll(lineStationEdges.stream()
            .map(it -> it.getLineStation().getStationId())
            .collect(Collectors.toList()));

        return stationIds;
    }

    public int calculateDuration() {
        return lineStationEdges.stream().mapToInt(it -> it.getLineStation().getDuration()).sum();
    }

    public int calculateDistance() {
        return lineStationEdges.stream().mapToInt(it -> it.getLineStation().getDistance()).sum();
    }

    public int calculateTotalFare() {
        int totalDistance = calculateDistance();
        int defaultDistanceFare = 1250;
        int overDistanceFare = calculateOverFare(totalDistance - 10);
        int maximumExtraFare = lineStationEdges.stream()
            .mapToInt(it -> it.getLineStation().getExtraFare()).max()
            .orElseThrow(() -> new IllegalArgumentException("extraFare가 설정되지 않았습니다."));
        LoginMember loginMember = (LoginMember) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        int notDiscountedFare = (defaultDistanceFare + overDistanceFare + maximumExtraFare);

        return (int) calculateDiscountedFare(notDiscountedFare, loginMember.getAge());
    }

    private int calculateOverFare(int distance) {
        if (distance <= 0) {
            return 0;
        }
        if (distance <= 40) {
            return (int) ((Math.ceil((distance - 1) / 5) + 1) * 100);
        }
        return (int) ((Math.ceil((distance - 40 - 1) / 8) + 1) * 100) + calculateOverFare(40);
    }

    private double calculateDiscountedFare(Integer notDiscountedFare, Integer age) {
        if (6 <= age && age < 13) {
            return (notDiscountedFare - 350) * 0.5;
        }
        if (13 <= age && age < 19) {
            return (notDiscountedFare - 350) * 0.2;
        }
        return 1;
    }
}
