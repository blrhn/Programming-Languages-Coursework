package pwr.edu.travels.client.service.utils;

// All ids and codes are taken from API's dictionaries
public class IndexMapper {

    // returns API's variable id
    private static int mapVariableIndex(int variableIndex) {
        return switch (variableIndex) {
            case 0 -> 1584;
            case 1 -> 1585;
            case 2 -> 1586;
            case 3 -> 1587;
            default -> 0;
        };
    }


    // returns API's section id
    private static int mapSectionIndex(int sectionIndex) {
        return switch (sectionIndex) {
            case 0 -> 1309;
            case 1 -> 1310;
            case 2 -> 1311;
            default -> 1428;
        };
    }

    // returns API's period code
    private static int mapPeriodIndex(int sectionIndex) {
        if (sectionIndex == 2) {
            return 331;
        } else {
            return 282;
        }
    }

    public static MappedIndex getMappedIndex(int variableIndex, int sectionIndex) {
        int variableId = mapVariableIndex(variableIndex);
        int sectionId = mapSectionIndex(sectionIndex);
        int periodIndex = mapPeriodIndex(sectionIndex);

        return new MappedIndex(variableId, sectionId, periodIndex);
    }
}