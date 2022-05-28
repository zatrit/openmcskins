package net.zatrit.openmcskins.io.util;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

public final class NetworkUtils {
    private static final Pattern URL_PATTERN = Pattern.compile("((f|ht)tp.?|file)/*:(/)*.*");

    private NetworkUtils() {
    }

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

    public static @NotNull String fixUrl(@NotNull String url) {
        if (URL_PATTERN.matcher(url).matches()) return url;
        return "http://" + url;
    }
}
