package environment.service;

import utils.other.HostPortPair;
import utils.other.Parser;

import java.util.ArrayList;
import java.util.List;

public class Environment implements IEnvironment {
    private final List<String> riverSectionList = new ArrayList<>();

    @Override
    public void assignRiverSection(int port, String host) {
        this.riverSectionList.add(Parser.createAddress(port, host));
    }

    public List<String> getRivers() {
        return riverSectionList;
    }

    public String getRiverSectionHost(int index) {
        return Parser.parseHost(riverSectionList.get(index));
    }

    public int getRiverSectionPort(int index) {
        return Parser.parsePort(riverSectionList.get(index));
    }

    public HostPortPair getRiverSectionHostPortPair(int index) {
        return new HostPortPair(getRiverSectionHost(index), getRiverSectionPort(index));
    }
}
