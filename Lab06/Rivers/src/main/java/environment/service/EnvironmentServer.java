package environment.service;

import utils.connection.ClientActivity;
import utils.connection.Server;
import utils.interfaces.Updatable;
import utils.other.HostPortPair;
import utils.other.Parser;

import java.io.IOException;
import java.net.Socket;

public class EnvironmentServer extends Server {
    private final Environment environment = new Environment();

    public EnvironmentServer(int port, Updatable updatable) {
        super(port, updatable);
    }

    @Override
    public void handleClient(Socket socket, String message) {
        try {
            if (message.startsWith("ars:")) {
                handleAssign(message);
            }

            socket.close();
        } catch (IOException e ) {
            e.printStackTrace();
        }
    }

    private void handleAssign(String message) {
        HostPortPair hp = Parser.parseAssign(message);
        environment.assignRiverSection(hp.port(), hp.host());
        getUpdatable().update();
    }

    public void setRainfall(int rainfall, int index) {
        HostPortPair hp = environment.getRiverSectionHostPortPair(index);
        ClientActivity.sendMessage("srf:" + rainfall, hp.host(), hp.port());
    }

    public Environment getEnvironment() {
        return environment;
    }
}
