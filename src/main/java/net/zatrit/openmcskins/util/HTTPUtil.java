package net.zatrit.openmcskins.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPUtil {
    public static int getResponseCode(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            return connection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
