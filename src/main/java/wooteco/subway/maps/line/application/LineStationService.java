package wooteco.subway.maps.line.application;

import com.google.common.collect.Lists;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wooteco.subway.maps.line.domain.Line;
import wooteco.subway.maps.line.domain.LineStation;
import wooteco.subway.maps.line.dto.LineStationCreateRequest;
import wooteco.subway.maps.station.application.StationService;
import wooteco.subway.maps.station.domain.Station;

@Service
@Transactional
public class LineStationService {

    private LineService lineService;
    private StationService stationService;

    public LineStationService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public void addLineStation(Long lineId, LineStationCreateRequest request) {
        checkAddLineStationValidation(request);

        Line line = lineService.findLineById(lineId);
        LineStation lineStation = new LineStation(request.getStationId(), request.getPreStationId(),
            request.getDistance(), request.getDuration(), line.getExtraFare());
        line.addLineStation(lineStation);
    }

    public void removeLineStation(Long lineId, Long stationId) {
        Line line = lineService.findLineById(lineId);
        line.removeLineStationById(stationId);
    }

    private void checkAddLineStationValidation(LineStationCreateRequest request) {
        Map<Long, Station> stations = stationService.findStationsByIds(
            Lists.newArrayList(request.getPreStationId(), request.getStationId()));

        if (stations.get(request.getStationId()) == null) {
            throw new RuntimeException();
        }
        if (request.getPreStationId() != null && stations.get(request.getPreStationId()) == null) {
            throw new RuntimeException();
        }
    }
}