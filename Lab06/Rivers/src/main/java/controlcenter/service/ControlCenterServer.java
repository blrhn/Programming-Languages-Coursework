package controlcenter.service;

import utils.connection.ClientActivity;
import utils.connection.Server;
import utils.interfaces.Updatable;
import utils.other.HostPortPair;
import utils.other.Parser;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ControlCenterServer extends Server {
    private final ControlCenter controlCenter = new ControlCenter();

    public ControlCenterServer(int port, Updatable updatable) {
        super(port, updatable);
    }

    @Override
    public void handleClient(Socket socket, String message) {
        try {
            if (message.startsWith("arb:")) {
                handleAssign(message);
            }
            socket.close();
        } catch (IOException e ) {
            e.printStackTrace();
        }
    }

    private void handleAssign(String message) {
        HostPortPair hp = Parser.parseAssign(message);
        controlCenter.assignRetentionBasin(hp.port(), hp.host());
        getUpdatable().update();
    }

    public ControlCenter getControlCenter() {
        return controlCenter;
    }

    public void setWaterDischarge(int waterDischarge, int index) {
        HostPortPair hp = controlCenter.getRetentionBasinHostPortPair(index);
        ClientActivity.sendMessage("swd:" + waterDischarge, hp.host(), hp.port());
    }

    private void getWaterDischarge(int index) {
        HostPortPair hp = controlCenter.getRetentionBasinHostPortPair(index);
        String waterDischarge = ClientActivity.queryClient("gwd", hp.host(), hp.port());
        controlCenter.updateWaterDischarge(index, waterDischarge);
    }

    private void getFillingPercentage(int index) {
        HostPortPair hp = controlCenter.getRetentionBasinHostPortPair(index);
        String fillingPercentage = ClientActivity.queryClient("gfp", hp.host(), hp.port());
        controlCenter.updateFillingPercentage(index, fillingPercentage);
    }

    private void updateRetentionBasins() {
        for (int i = 0; i < controlCenter.getRetentionBasins().size(); i++) {
            getWaterDischarge(i);
            getFillingPercentage(i);
        }
        getUpdatable().update();
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
