package pwr.edu.travels.client.service.parser;

import pwr.edu.travels.client.model.Result;

import java.util.List;

public class Parser {
    public static List<Result> getResults(int variableIndex, int sectionIndex, int year, int page) {
        if (variableIndex == 0) {
            return ParticipationParser.getParticipations(variableIndex, sectionIndex, year, page);
        } else {
            return TourismParser.getTourisms(variableIndex, sectionIndex, year, page);
        }
    }

    public static List<Integer> getPageData() {
        return BaseParser.pageData;
    }

    public static String getWayOfPresentation() {
        return BaseParser.unit;
    }

    public static List<String> getColumnNames() {
        return BaseParser.columnNames;
    }
}
