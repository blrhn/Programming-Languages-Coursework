package riversection.service;

import interfaces.IRetensionBasin;
import interfaces.IRiverSection;
import interfaces.ITailor;
import utils.Updatable;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

import java.util.Map;

public class RiverSection extends UnicastRemoteObject implements IRiverSection {
    private ITailor it;

    private final int[] waterInSection;
    private final String name;
    private final Map<String, IRetensionBasin> retensionBasinMap = new HashMap<>();
    private final Updatable updatable;
    private final String host;
    private final int port;

    public RiverSection(String name, int delay, Updatable updatable, String host, int port) throws RemoteException {
        super();
        this.waterInSection = new int[delay];
        this.name = name;
        this.updatable = updatable;
        this.host = host;
        this.port = port;
    }

    @Override
    public void setRealDischarge(int realDischarge) throws RemoteException {
        updatable.update();
        moveWater();
        waterInSection[0] = realDischarge;

        int water = getLastWater();
        if (water > 0) {
            sendWater(water);
        }
    }

    @Override
    public void setRainfall(int rainfall) throws RemoteException {
        updatable.update();
        sendWater(rainfall);
    }

    @Override
    public void assignRetensionBasin(IRetensionBasin irb, String name) {
        retensionBasinMap.put(name, irb);
    }

    public void sendWater(int inflow) throws RemoteException {
        if (!retensionBasinMap.isEmpty()) {
            inflow = inflow / retensionBasinMap.size();
            for (IRetensionBasin basin : retensionBasinMap.values()) {
                basin.setWaterInflow(inflow, this.name);
            }
        }
    }

    public void moveWater() {
        for (int i = waterInSection.length - 1; i > 0; i--) {
            waterInSection[i] = waterInSection[i - 1];
        }
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
    }

    public void connect(String name) throws RemoteException {
        it.register(this, name);
    }

    public int[] getWater() {
        return waterInSection;
    }

    public int getLastWater() {
        return waterInSection[waterInSection.length - 1];
    }
}
