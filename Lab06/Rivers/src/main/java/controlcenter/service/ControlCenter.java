package controlcenter.service;

import utils.other.HostPortPair;
import utils.other.Parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControlCenter implements IControlCenter {
    private final List<String> retentionBasins = new ArrayList<>();
    private final Map<Integer, String> fillingPercentage = new HashMap<>();
    private final Map<Integer, String> waterDischarge = new HashMap<>();

    @Override
    public void assignRetentionBasin(int port, String host) {
        this.retentionBasins.add(Parser.createAddress(port, host));
    }

    public List<String> getRetentionBasins() {
        return retentionBasins;
    }

    public String getRetentionBasinHost(int index) {
        return Parser.parseHost(retentionBasins.get(index));
    }

    public int getRetentionBasinPort(int index) {
        return Parser.parsePort(retentionBasins.get(index));
    }

    public HostPortPair getRetentionBasinHostPortPair(int index) {
        return new HostPortPair(getRetentionBasinHost(index), getRetentionBasinPort(index));
    }

    public void updateFillingPercentage(int index, String value) {
        fillingPercentage.put(index, value);
    }

    public void updateWaterDischarge(int index, String value) {
        waterDischarge.put(index, value);
    }

    public Map<Integer, String> getFillingPercentage() {
        return fillingPercentage;
    }

    public Map<Integer, String> getWaterDischarge() {
        return waterDischarge;
    }
}
