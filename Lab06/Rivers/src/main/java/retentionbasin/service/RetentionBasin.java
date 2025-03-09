package retentionbasin.service;

import utils.other.Parser;

import java.util.HashMap;
import java.util.Map;

public class RetentionBasin implements IRetentionBasin {
    private final int volume;
    private int discharge;
    private int fill;
    private int realDischarge;
    private int currentInflow;

    private String outRiverSection;
    private final Map<Integer, Integer> riverInflowMap = new HashMap<>();

    public RetentionBasin(int volume) {
        this.volume = volume;
        this.fill = 0;
        this.realDischarge = 0;
    }

    @Override
    public int getWaterDischarge() {
        return realDischarge;
    }

    @Override
    public long getFillingPercentage() {
        return Math.round((double) fill * 100 / volume);
    }

    @Override
    public void setWaterDischarge(int waterDischarge) {
        this.discharge = waterDischarge;
    }

    @Override
    public void setWaterInflow(int waterInflow, int port) {
        riverInflowMap.put(port, waterInflow);
        fill();
        updateCurrentInflow();
    }

    @Override
    public void assignRiverSection(int port, String host) {
        outRiverSection = Parser.createAddress(port, host);
    }

    public boolean checkDischarge() {
        if (fill > volume) {
            realDischarge = currentInflow;
            fill -= realDischarge;
            if (fill < 0) {
                fill = 0;
            }
        } else if (fill < discharge) {
            realDischarge = fill;
            fill = 0;
        } else if (fill == 0) {
            realDischarge = 0;
            return false;
        } else {
            realDischarge = discharge;
            fill -= realDischarge;
        }

        return true;
    }

    public String getOutRiverSection() {
        return outRiverSection;
    }

    private void fill() {
        this.fill += riverInflowMap.values().stream().mapToInt(v -> v).sum();
    }

    private void updateCurrentInflow() {
        this.currentInflow = riverInflowMap.values().stream().mapToInt(v -> v).sum();
    }
}
