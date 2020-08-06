package wooteco.subway.maps.map.domain;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Collectors;
import wooteco.security.core.context.SecurityContextHolder;
import wooteco.subway.members.member.domain.LoginMember;

public class SubwayPath {

    private static final int DEFAULT_DISTANCE_FARE = 1250;
    private static final int DEFAULT_DISTANCE = 10;
    private static final int CHILDREN_MIN_AGE = 6;
    private static final int CHILDREN_MAX_AGE = 12;
    private static final int YOUTH_MIN_AGE = 13;
    private static final int YOUTH_MAX_AGE = 18;
    private static final int YOUNG_DEFAULT_DISCOUNT = 350;
    private static final double YOUTH_DISCOUNT_RATE = 0.2;
    private static final double CHILDREN_DISCOUNT_RATE = 0.5;
    private static final int OVER_FARE_UNIT = 100;

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

    public int calculateFare() {
        int overDistanceFare = calculateOverFare(calculateDistance() - DEFAULT_DISTANCE);
        int maximumExtraFare = lineStationEdges.stream()
            .mapToInt(it -> it.getLineStation().getExtraFare()).max()
            .orElseThrow(() -> new IllegalArgumentException("extraFare가 설정되지 않았습니다."));

        LoginMember loginMember;
        try {
            loginMember = (LoginMember) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        } catch (NullPointerException e) {
            loginMember = new LoginMember(null, "", "", 20);
        }
        int notDiscountedFare = (DEFAULT_DISTANCE_FARE + overDistanceFare + maximumExtraFare);

        return calculateDiscountedFare(notDiscountedFare, loginMember.getAge());
    }

    private int calculateOverFare(int distance) {
        if (distance <= 0) {
            return 0;
        }
        if (distance <= 40) {
            return (int) ((Math.ceil((distance - 1) / 5) + 1) * OVER_FARE_UNIT);
        }
        return (int) ((Math.ceil((distance - 40 - 1) / 8) + 1) * OVER_FARE_UNIT)
            + calculateOverFare(40);
    }

    private int calculateDiscountedFare(int notDiscountedFare, int age) {
        if (CHILDREN_MIN_AGE <= age && age <= CHILDREN_MAX_AGE) {
            return (int) ((notDiscountedFare - YOUNG_DEFAULT_DISCOUNT) * CHILDREN_DISCOUNT_RATE);
        }
        if (YOUTH_MIN_AGE <= age && age <= YOUTH_MAX_AGE) {
            return (int) ((notDiscountedFare - YOUNG_DEFAULT_DISCOUNT) * YOUTH_DISCOUNT_RATE);
        }
        return 1;
    }
}
