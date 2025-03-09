package riversection.service;

import utils.other.Parser;

import java.util.ArrayList;
import java.util.List;

public class RiverSection implements IRiverSection {
    private int rainfall;
    private int discharge;
    private final int[] waterInSection;

    private final List<String> retentionBasins = new ArrayList<>();

    public RiverSection(int delay) {
        this.waterInSection = new int[delay];
    }

    @Override
    public void setRealDischarge(int realDischarge) {
        moveWater();
        this.discharge = realDischarge;
        waterInSection[0] = realDischarge;
    }

    @Override
    public void setRainfall(int rainfall) {
        this.rainfall = rainfall;
    }

    @Override
    public void assignRetentionBasin(int port, String host) {
        retentionBasins.add(Parser.createAddress(port, host));
    }

    public List<String> getRetentionBasins() {
        return retentionBasins;
    }

    public void moveWater() {
        for (int i = waterInSection.length - 1; i > 0; i--) {
            waterInSection[i] = waterInSection[i - 1];
        }
    }

    public int[] getWater() {
        return waterInSection;
    }

    public int getLastWater() {
        return waterInSection[waterInSection.length - 1];
    }
}
