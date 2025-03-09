package utils.connection;

import utils.interfaces.Updatable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class Server {
    private Thread thread = null;
    private ServerSocket serverSocket = null;
    private final int port;
    private boolean isRunning = true;
    private final Updatable updatable;

    public Server(int port, Updatable updatable) {
        this.port = port;
        this.updatable = updatable;
    }

    public void start() {
        isRunning = true;
        thread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);

                while (isRunning) {
                    Socket socket = serverSocket.accept();
                    InputStream inputStream = socket.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String message = bufferedReader.readLine();
                    System.out.println(message);
                    handleClient(socket, message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public abstract void handleClient(Socket socket, String message);

    public Updatable getUpdatable() {
        return updatable;
    }
}
