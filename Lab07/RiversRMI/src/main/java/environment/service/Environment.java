package environment.service;

import interfaces.IEnvironment;
import interfaces.IRiverSection;
import interfaces.ITailor;
import utils.Updatable;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Environment extends UnicastRemoteObject implements IEnvironment {
    private final List<String> riverSectionList = new ArrayList<>();
    private final Map<String, IRiverSection> riverSectionMap = new HashMap<>();
    private final Updatable updatable;
    private final String host;
    private final int port;
    private ITailor it;

    public Environment(Updatable updatable, String host, int port) throws RemoteException {
        super();
        this.updatable = updatable;
        this.host = host;
        this.port = port;
    }

    public List<String> getRivers() {
        return riverSectionList;
    }

    public boolean register(String name) {
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            it = (ITailor) registry.lookup("Tailor");
            return it.register(this, name);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void unregister() throws RemoteException {
        it.unregister(this);
        riverSectionList.clear();
        riverSectionMap.clear();
    }

    public void sendRainfall(String name, int rainfall) throws RemoteException {
        riverSectionMap.get(name).setRainfall(rainfall);
    }

    @Override
    public void assignRiverSection(IRiverSection irs, String name) throws RemoteException {
        this.riverSectionMap.put(name, irs);
        this.riverSectionList.add(name);
        updatable.update();
    }
}
