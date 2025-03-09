package pwr.edu.travels.client.service.parser;

import pwr.edu.travels.client.model.Reference;
import pwr.edu.travels.client.model.Result;
import pwr.edu.travels.client.model.base.BaseData;
import pwr.edu.travels.client.model.base.BaseResponse;
import pwr.edu.travels.client.service.api.DataRetrieverImpl;
import pwr.edu.travels.client.service.api.IMetadataRetriever;
import pwr.edu.travels.client.service.api.IResponseRetriever;
import pwr.edu.travels.client.service.utils.IndexMapper;
import pwr.edu.travels.client.service.utils.MappedIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
/*
T - BaseData/TourismData
U - BaseResponse/TourismResponse
https://docs.oracle.com/javase/8/docs/api/java/util/function/Function.html#andThen-java.util.function.Function-
*/

public class BaseParser {
    protected static List<Integer> pageData = new ArrayList<>();
    protected static String unit = "";
    protected static List<String> columnNames = new ArrayList<>();

    private static final String noAnnotation;
    private static final String noData;

    static {
        IMetadataRetriever annotationRetriever = new DataRetrieverImpl<>(Reference.class);
        noAnnotation = annotationRetriever.getDataAnnotation(253);
        noData = annotationRetriever.getDataAnnotation(38);
    }

    // returns gathered and combined data (fetched data from reference + units + annotations)
    protected static <T extends BaseData, U extends BaseResponse> List<Result> parseData(
            int variableIndex, int sectionIndex, int year, int page, Class<U> responseClass,
            Function<U, List<T>> dataExtractor,
            Function<T, List<Integer>> dimensionExtractor,
            Function<T, List<Long>> positionExtractor) {

        MappedIndex mappedIndex = IndexMapper.getMappedIndex(variableIndex, sectionIndex);

        IMetadataRetriever referencesRetriever = new DataRetrieverImpl<>(Reference.class);
        List<Reference> references = referencesRetriever.getReferences(mappedIndex.sectionId());

        IResponseRetriever<U> responseRetriever = new DataRetrieverImpl<>(responseClass);
        U response = responseRetriever.getResponseData(mappedIndex.variableId(), mappedIndex.sectionId(), year,
                                                       mappedIndex.period(), page);

        if (references != null && response != null) {
            pageData = response.getPageInfo();

            Map<Integer, String> dimensionsMap = getDimensionsMap(references);
            Map<Long, String> positionsMap = getPositionsMap(references);

            List<T> data = dataExtractor.apply(response);

            unit = referencesRetriever.getWayOfPresentation(data.getFirst().getWayOfPresentationId());
            List<Integer> dimensions = dimensionExtractor.apply(data.getFirst());
            columnNames = dimensions.stream().map(dimensionsMap::get).collect(Collectors.toList());

            return generateResults(data, positionExtractor, positionsMap);
        }

        return null;
    }

    // fetches data from reference
    private static Map<Integer, String> getDimensionsMap(List<Reference> references) {
        Map<Integer, String> dimensionsMap = new HashMap<>();

        for (Reference ref : references) {
            dimensionsMap.put(ref.getDimensionId(), ref.getDimensionName());
        }

        return dimensionsMap;
    }

    // fetches data from reference
    private static Map<Long, String> getPositionsMap(List<Reference> references) {
        Map<Long, String> positionsMap = new HashMap<>();

        for (Reference ref : references) {
            positionsMap.put(ref.getPositionId(), ref.getPositionName());
        }

        return positionsMap;
    }

    // combines the data together
    private static <T extends BaseData> List<Result> generateResults(List<T> data,
                                                                     Function<T, List<Long>> positionExtractor,
                                                                     Map<Long, String> positionsMap) {
        List<Result> results = new ArrayList<>();

        for (T tData : data) {
            List<String> stringPositions = positionExtractor.apply(tData).stream().
                    map(positionsMap::get).collect(Collectors.toList());

            float value = tData.getValue();
            String annotation = tData.getNoDataId() == 38 ? noData : noAnnotation;

            results.add(new Result(stringPositions, value, annotation));
        }

        return results;
    }
}
