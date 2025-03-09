package retentionbasin.service;

import utils.connection.ClientActivity;
import utils.connection.Server;
import utils.interfaces.Updatable;
import utils.other.HostPortPair;
import utils.other.Parser;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class RetentionBasinServer extends Server {
    private final String host;
    private final int port;
    private final RetentionBasin retentionBasin;

    public RetentionBasinServer(String host, int port, int volume, Updatable updatable) {
        super(port, updatable);
        this.host = host;
        this.port = port;
        retentionBasin = new RetentionBasin(volume);
    }

    @Override
    public void handleClient(Socket socket, String message) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            if (message.startsWith("arb:")) {
                handleAssign(message);
            } else if (message.startsWith("swi:")) {
                handleSetWaterInflow(message);
            } else if (message.startsWith("gwd")) {
                handleWaterDischarge(out);
            } else if (message.startsWith("gfp")) {
                handleFillingPercentage(out);
            } else if (message.startsWith("swd:")) {
                handleSetWaterDischarge(message);
            }
            socket.close();
        } catch (IOException e ) {
            e.printStackTrace();
        }
    }

    private void handleAssign(String message) {
        HostPortPair hp = Parser.parseAssign(message);
        retentionBasin.assignRiverSection(hp.port(), hp.host());
    }

    private void handleSetWaterInflow(String message) {
        int waterInflow = Parser.parseFirstInt(message);
        int waterPort = Integer.parseInt(message.split(",")[1]);

        retentionBasin.setWaterInflow(waterInflow, waterPort);

        if (retentionBasin.checkDischarge()) {
            String outRiver = retentionBasin.getOutRiverSection();
            if (outRiver != null) {
                String outHost = Parser.parseHost(outRiver);
                int outPort = Parser.parsePort(outRiver);
                fillRiverSection(retentionBasin.getWaterDischarge(), outHost, outPort);
            }
        }
        getUpdatable().update();
    }

    private void handleWaterDischarge(PrintWriter out) {
        int waterDischarge = retentionBasin.getWaterDischarge();
        out.println(waterDischarge);
    }

    private void handleFillingPercentage(PrintWriter out) {
        long fillingPercentage = retentionBasin.getFillingPercentage();
        out.println(fillingPercentage);
    }

    private void handleSetWaterDischarge(String message) {
        int discharge = Parser.parseSet(message);
        retentionBasin.setWaterDischarge(discharge);
    }

    public void assignRetentionBasin(String serverHost, int serverPort) {
        ClientActivity.sendMessage("arb:" + port + "," + host, serverHost, serverPort);
    }

    public void fillRiverSection(int discharge, String outRiverSectionHost, int outRiverSectionPort) {
        ClientActivity.sendMessage("srd:" + discharge, outRiverSectionHost, outRiverSectionPort);
    }

    public RetentionBasin getRetentionBasin() {
        return retentionBasin;
    }
}
