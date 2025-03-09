package tailor.service;

import interfaces.*;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tailor implements ITailor {
    private final Map<String, Remote> registryMap = new HashMap<>();
    private final Map<String, String> proxyNameMap = new HashMap<>();

    private final int port;

    public Tailor(int port) {
        this.port = port;
    }

    /*
    this implementation differs from instruction's requirements as the name argument mean the name of remote being
    interacted with, rather than the given remote's name
    */
    @Override
    public boolean register(Remote r, String name) throws RemoteException {
        if (r instanceof IControlCenter) {
            if (!registryMap.containsKey(name)) {
                registryMap.put(name, r);
                return true;
            }
        } else if (r instanceof IEnvironment) {
            if (!registryMap.containsKey(name)) {
                registryMap.put(name, r);
                return true;
            }
        } else if (r instanceof IRetensionBasin) {
            String endpoint = getRemoteEndpoint(r.toString());
            return registerRetensionBasin(r, name, endpoint);
        } else if (r instanceof IRiverSection) {
            String endpoint = getRemoteEndpoint(r.toString());
            return registerRiverSection(r, name, endpoint);
        }

        return false;
    }

    @Override
    public boolean unregister(Remote r) throws RemoteException {
        for (Map.Entry<String, Remote> entry : registryMap.entrySet()) {
            if (entry.getValue().equals(r)) {
                String name = entry.getKey();
                registryMap.remove(name);
                String endpoint = getRemoteEndpoint(r.toString());
                proxyNameMap.remove(endpoint);
            }
            return true;
        }
        return false;
    }

    private boolean registerRetensionBasin(Remote r, String name, String endpoint) throws RemoteException {
        if (registryMap.containsKey(name)) {
            Remote registry = registryMap.get(name);

            if (registry instanceof IControlCenter) {
                ((IControlCenter) registry).assignRetensionBasin((IRetensionBasin) r, proxyNameMap.get(endpoint));
            } else if (registry instanceof IRiverSection) {
                ((IRiverSection) registry).assignRetensionBasin((IRetensionBasin) r, proxyNameMap.get(endpoint));
            }
            return false;
        } else {
            registryMap.put(name, r);
            proxyNameMap.put(endpoint, name);
            return true;
        }
    }

    private boolean registerRiverSection(Remote r, String name, String endpoint) throws RemoteException {
        if (registryMap.containsKey(name)) {
            Remote registry = registryMap.get(name);

            if (registry instanceof IEnvironment) {
                ((IEnvironment) registry).assignRiverSection((IRiverSection) r, proxyNameMap.get(endpoint));
            } else if (registry instanceof IRetensionBasin) {
                ((IRetensionBasin) registry).assignRiverSection((IRiverSection) r, proxyNameMap.get(endpoint));
            }
            return false;
        } else {
            registryMap.put(name, r);
            proxyNameMap.put(endpoint, name);
            return true;
        }
    }

    private String getRemoteEndpoint(String proxy) {
        String finder = null;

        Pattern pattern = Pattern.compile(".*endpoint:\\[(.*)\\]\\(remote.*");
        Matcher matcher = pattern.matcher(proxy);
        if (matcher.find())
        {
            finder = matcher.group(1);
        }

        return finder;
    }

    public void initializeTailor() {
        try {
            ITailor it = (ITailor) UnicastRemoteObject.exportObject(this, 0);
            Registry r = LocateRegistry.createRegistry(port);
            r.rebind("Tailor", it);

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
