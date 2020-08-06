package wooteco.subway.maps.map.documentation;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.web.context.WebApplicationContext;
import wooteco.security.core.TokenResponse;
import wooteco.subway.common.documentation.Documentation;
import wooteco.subway.maps.map.application.MapService;
import wooteco.subway.maps.map.application.PathService;
import wooteco.subway.maps.map.dto.PathResponse;
import wooteco.subway.maps.map.ui.MapController;
import wooteco.subway.maps.station.dto.StationResponse;

@WebMvcTest(controllers = {MapController.class})
public class PathDocumentation extends Documentation {

    @Autowired
    private MapController mapController;
    @MockBean
    private PathService pathService;
    @MockBean
    private MapService mapService;

    protected TokenResponse tokenResponse;

    @BeforeEach
    public void setUp(
        WebApplicationContext context, RestDocumentationContextProvider restDocumentation) {
        super.setUp(context, restDocumentation);
        tokenResponse = new TokenResponse("token");
    }

    @Test
    void findPath() {
        PathResponse pathResponse = new PathResponse(
            Arrays.asList(
                new StationResponse(1L, "강남역", null, null),
                new StationResponse(2L, "역삼역", null, null)
            ), 1, 2);

        when(mapService.findPath(any(), any(), any())).thenReturn(pathResponse);

        Long sourceId = 1L;
        Long targetId = 2L;
        String type = "DISTANCE";

        given().log().all().
            header("Authorization", "Bearer " + tokenResponse.getAccessToken()).
            accept(MediaType.APPLICATION_JSON_VALUE).
            when().
            get("/paths?source=" + sourceId + "&target=" + targetId + "&type=" + type).
            then().
            log().all().
            apply(document("paths",
                getDocumentRequest(),
                getDocumentResponse(),
                requestHeaders(
                    headerWithName("Authorization").description("Bearer auth credentials")),
                responseFields(
                    fieldWithPath("stations.[]").type(JsonFieldType.ARRAY).description("역 리스트"),
                    fieldWithPath("stations.[].id").type(JsonFieldType.NUMBER).description("역 아이디"),
                    fieldWithPath("stations.[].name").type(JsonFieldType.STRING)
                        .description("역 이름"),
                    fieldWithPath("duration").type(JsonFieldType.NUMBER).description("이동 시간"),
                    fieldWithPath("distance").type(JsonFieldType.NUMBER).description("이동 거리"),
                    fieldWithPath("totalFare").type(JsonFieldType.NUMBER).description("총 요금")
                ))).
            extract();
    }
}
