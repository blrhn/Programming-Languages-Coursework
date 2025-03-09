package pwr.edu.travels.client.service.parser;

import pwr.edu.travels.client.model.base.BaseData;
import pwr.edu.travels.client.model.Result;
import pwr.edu.travels.client.model.participation.ParticipationResponse;
import java.util.List;

public class ParticipationParser extends BaseParser {
    protected static List<Result> getParticipations(int variableIndex, int sectionIndex, int year, int page) {
        return parseData(
                variableIndex, sectionIndex, year, page,
                ParticipationResponse.class,
                ParticipationResponse::getParticipationData,
                BaseData::getDimensionIds,
                BaseData::getPositionIds
        );
    }
}
