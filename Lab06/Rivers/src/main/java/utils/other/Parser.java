package utils.other;

public class Parser {
    public static String parseHost(String address) {
        return address.split(",")[1];
    }

    public static int parsePort(String address) {
        return Integer.parseInt(address.split(",")[0]);
    }

    public static String createAddress(int port, String host) {
        return port + "," + host;
    }

    public static HostPortPair parseAssign(String message) {
        String address = message.substring(4);
        String host = Parser.parseHost(address);
        int port = Parser.parsePort(address);

        return new HostPortPair(host, port);
    }

    public static int parseSet(String message) {
        return Integer.parseInt(message.substring(4));
    }

    public static int parseFirstInt(String message) {
        return Integer.parseInt(message.substring(4).split(",")[0]);
    }
}
