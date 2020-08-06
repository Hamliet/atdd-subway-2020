package wooteco.subway.maps.map.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wooteco.security.core.Authentication;
import wooteco.security.core.context.SecurityContextHolder;
import wooteco.security.core.userdetails.UserDetails;
import wooteco.subway.maps.line.domain.LineStation;
import wooteco.subway.members.member.application.CustomUserDetailsService;
import wooteco.subway.members.member.application.MemberService;
import wooteco.subway.members.member.domain.Member;
import wooteco.subway.members.member.domain.MemberRepository;
import wooteco.subway.members.member.dto.MemberRequest;
import wooteco.subway.members.member.dto.MemberResponse;

@SpringBootTest
class SubwayPathTest {

    private static final String EMAIL = "email@email.com";
    private static final String PASSWORD = "password";
    private static final Integer AGE = 10;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    MemberService memberService;

    private Member createMember(String email, String nickname, int age) {
        MemberRequest memberRequest = new MemberRequest(email, nickname, age);
        MemberResponse memberResponse = memberService.createMember(memberRequest);

        return memberRepository.findById(memberResponse.getId())
            .orElseThrow(() -> new IllegalArgumentException("해당하는 사용자가 존재하지 않습니다."));
    }

    private void setAuthentication(Member member) {
        CustomUserDetailsService customUserDetailsService = new CustomUserDetailsService(
            memberRepository);
        UserDetails userDetails = customUserDetailsService
            .loadUserByUsername(member.getEmail());
        Authentication authentication = new Authentication(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void calculateTotalFare() {
        List<LineStationEdge> lineStationEdges = Arrays.asList(
            new LineStationEdge(new LineStation(1L, null, 4, 1, 100), 1L),
            new LineStationEdge(new LineStation(2L, 1L, 5, 1, 300), 1L),
            new LineStationEdge(new LineStation(3L, 2L, 15, 1, 500), 1L)
        );
        SubwayPath subwayPath = new SubwayPath(lineStationEdges);
        Member member = createMember(EMAIL, PASSWORD, AGE);
        setAuthentication(member);

        // 어린이, 노선 추가 요금 500, 이용 거리 15km 추가일 경우
        // (기본 운임 1250 + 추가 이용 금액 300 + 노선 추가 요금 500 + 어린이 할인 금액 350) * 어린이 할인 비율 0.5
        assertThat(subwayPath.calculateTotalFare()).isEqualTo(850);
    }
}