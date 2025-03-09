module client {
    requires com.google.gson;
    requires java.net.http;

    exports pwr.edu.travels.client.service.utils;
    exports pwr.edu.travels.client.service.parser;
    exports pwr.edu.travels.client.model;
    exports pwr.edu.travels.client.model.base;

    opens pwr.edu.travels.client.model to com.google.gson;
    opens pwr.edu.travels.client.model.participation to com.google.gson;
    opens pwr.edu.travels.client.model.tourism to com.google.gson;
    opens pwr.edu.travels.client.model.base to com.google.gson;

}