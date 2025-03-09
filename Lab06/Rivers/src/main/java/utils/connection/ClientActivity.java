package utils.connection;

public class ClientActivity {
    public static void sendMessage(String message, String host, int port) {
        Client client = new Client();
        client.sendInformation(message, host, port);
    }

    public static String queryClient(String message, String host, int port) {
        Client client = new Client();
        return client.sendInformationAndGetResponse(message, host, port);
    }
}
