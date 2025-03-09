package pwr.edu.travels.client.service.parser;

import pwr.edu.travels.client.model.Result;
import pwr.edu.travels.client.model.tourism.TourismData;
import pwr.edu.travels.client.model.tourism.TourismResponse;

import java.util.List;

public class TourismParser extends BaseParser {
    protected static List<Result> getTourisms(int variableIndex, int sectionIndex, int year, int page) {
        return parseData(
                variableIndex, sectionIndex, year, page,
                TourismResponse.class,
                TourismResponse::getTourismData,
                TourismData::getTourismDimensionIds,
                TourismData::getTourismPositionIds
        );
    }
}
