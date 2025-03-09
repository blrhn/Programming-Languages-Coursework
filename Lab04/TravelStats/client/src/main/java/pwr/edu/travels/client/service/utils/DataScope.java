package pwr.edu.travels.client.service.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataScope {
    private static final int[] yearRange = {2014, 2022};
    private static final String[] variables = {"Uczestnictwo w wyjazdach turystycznych", "Podróże turystyczne", "Noclegi turystyczne",
            "Wydatki turystyczne"};
    private static final String[] section = {"Środek transportu", "Rodzaj zakwaterowania", "Sposób rezerwacji"};

    public static List<String> getVariables() {
        return Arrays.stream(variables).toList();
    }

    public static List<String> getLastSection() {
        return Arrays.stream(section).toList();
    }

    public static List<Integer> getPossibleYears(int index) {
        List<Integer> years = new ArrayList<>();
        int addYears = index == 2 ? 3 : 1;

        for (int i = yearRange[0]; i < yearRange[1]; i += addYears) {
            years.add(i);
        }

        return years;
    }
}
