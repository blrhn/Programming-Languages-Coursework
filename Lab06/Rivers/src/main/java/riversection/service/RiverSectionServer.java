package riversection.service;

import utils.connection.ClientActivity;
import utils.connection.Server;
import utils.interfaces.Updatable;
import utils.other.HostPortPair;
import utils.other.Parser;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class RiverSectionServer extends Server {
    String host;
    int port;
    RiverSection riverSection;

    public RiverSectionServer(String host, int port, int delay, Updatable updatable) {
        super(port, updatable);
        this.host = host;
        this.port = port;
        riverSection = new RiverSection(delay);
    }

    @Override
    public void handleClient(Socket socket, String message) {
        try {
            if (message.startsWith("srf:")) {
                handleSetRainfall(message);
            } else if (message.startsWith("arb:")) {
                handleAssign(message);
            } else if (message.startsWith("srd:")) {
                handleSetWaterDischarge(message);
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleSetRainfall(String message) {
        int rainfall = Parser.parseSet(message);
        riverSection.setRainfall(rainfall);
        getUpdatable().update();
        sendWater(rainfall);
    }

    private void handleAssign(String message) {
        HostPortPair hp = Parser.parseAssign(message);
        riverSection.assignRetentionBasin(hp.port(), hp.host());
    }

    private void handleSetWaterDischarge(String message) {
        getUpdatable().update();
        int discharge = Parser.parseSet(message);
        riverSection.setRealDischarge(discharge);

        int water = riverSection.getLastWater();
        if (water > 0) {
            sendWater(water);
        }
    }

    public void assignRiverSection(String riverSectionHost, int riverSectionPort) {
        ClientActivity.sendMessage("ars:" + port + "," + host, riverSectionHost, riverSectionPort);
    }

    public void assignRetentionBasin(String retentionBasinHost, int retentionBasinPort) {
        ClientActivity.sendMessage("arb:" + port + "," + host, retentionBasinHost, retentionBasinPort);
    }

    public void sendWater(int rainfall) {
        List<String> retentionBasins = riverSection.getRetentionBasins();
        rainfall = rainfall / retentionBasins.size();
        for (String retentionBasin : retentionBasins) {
            String inHost = Parser.parseHost(retentionBasin);
            int inPort = Parser.parsePort(retentionBasin);
            ClientActivity.sendMessage("swi:" + rainfall + "," + port, inHost, inPort);
        }
    }

    public RiverSection getRiverSection() {
        return riverSection;
    }
}
