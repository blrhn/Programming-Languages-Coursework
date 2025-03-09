package retentionbasin.service;

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

public class RetentionBasin extends UnicastRemoteObject implements IRetensionBasin {
    private ITailor it;

    private final int volume;
    private int discharge;
    private final String name;
    private final Updatable updatable;
    private final String host;
    private final int port;

    private int fill;
    private int realDischarge;
    private int currentInflow;

    private final Map<String, IRiverSection> outRiverSectionMap = new HashMap<>();
    private final Map<String, Integer> riverInflowMap = new HashMap<>();

    public RetentionBasin(int volume, String name, Updatable updatable, String host, int port) throws RemoteException {
        super();
        this.volume = volume;
        this.name = name;
        this.fill = 0;
        this.realDischarge = 0;
        this.updatable = updatable;
        this.host = host;
        this.port = port;
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
    public void setWaterInflow(int waterInflow, String name) throws RemoteException {
        riverInflowMap.put(name, waterInflow);
        fill();
        updateCurrentInflow();
        if (checkDischarge()) {
            sendDischarge();
        }

        updatable.update();
    }

    private void sendDischarge() throws RemoteException {
        outRiverSectionMap.forEach((name, riverSection) -> {
            try {
                if (name != null) {
                    riverSection.setRealDischarge(realDischarge);
                }
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void assignRiverSection(IRiverSection irs, String name) throws RemoteException {
        this.outRiverSectionMap.put(name, irs);
    }

    public boolean checkDischarge() {
        if (fill > volume) {
            realDischarge = currentInflow;
            fill -= realDischarge;
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
        fill = Math.max(fill, 0);

        return true;
    }

    public boolean register(String name, int volume) {
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

    private void fill() {
        this.fill += riverInflowMap.values().stream().mapToInt(v -> v).sum();
    }

    private void updateCurrentInflow() {
        this.currentInflow = riverInflowMap.values().stream().mapToInt(v -> v).sum();
    }
}
