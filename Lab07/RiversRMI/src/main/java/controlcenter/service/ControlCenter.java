package controlcenter.service;

import interfaces.IControlCenter;
import interfaces.IRetensionBasin;
import interfaces.ITailor;
import utils.Updatable;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ControlCenter extends UnicastRemoteObject implements IControlCenter {
    private final List<String> retentionBasins = new ArrayList<>();
    private final Map<String, String> basinInfo = new LinkedHashMap<>();
    private final Map<String, IRetensionBasin> retensionBasinMap = new HashMap<>();
    private final Updatable updatable;
    private final String host;
    private final int port;
    private ITailor it;

    public ControlCenter(Updatable updatable, String host, int port) throws RemoteException {
        super();
        this.updatable = updatable;
        this.host = host;
        this.port = port;
    }

    public List<String> getRetentionBasins() {
        return retentionBasins;
    }

    public Map<String, String> getBasinInfo() {
        return basinInfo;
    }

    public void sendDischarge(String name, int discharge) throws RemoteException {
        retensionBasinMap.get(name).setWaterDischarge(discharge);
    }

    @Override
    public void assignRetensionBasin(IRetensionBasin irb, String name) throws RemoteException {
        this.retensionBasinMap.put(name, irb);
        this.retentionBasins.add(name);
        updatable.update();
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
        retentionBasins.clear();
        basinInfo.clear();
        retensionBasinMap.clear();
    }

    private void updateRetentionBasins() {
        try {
            for (Map.Entry<String, IRetensionBasin> entry : retensionBasinMap.entrySet()) {
                String name = entry.getKey();
                IRetensionBasin irb = entry.getValue();
                String info = irb.getWaterDischarge() + ":" + irb.getFillingPercentage();

                basinInfo.put(name, info);
            }
            updatable.update();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    schedules a task that calls getWaterDischarge() and getFillingPercentage()
    for each RetentionBasin every two seconds
    */
    public void scheduleUpdates() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1, runnable -> {
            Thread thread = Executors.defaultThreadFactory().newThread(runnable);
            thread.setDaemon(true);
            return thread;
        });
        scheduler.scheduleAtFixedRate(this::updateRetentionBasins, 0, 2, TimeUnit.SECONDS);
    }
}
