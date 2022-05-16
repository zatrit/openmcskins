package net.zatrit.openmcskins.util.io;

import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.ProfileLookupCallback;
import net.zatrit.openmcskins.annotation.KeepClassMember;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
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

    public static UUID getUUIDByName(@NotNull GameProfileRepository repo, String name) {
        AtomicReference<UUID> uuid = new AtomicReference<>();

        repo.findProfilesByNames(new String[]{name}, Agent.MINECRAFT, new ProfileLookupCallback() {
            @KeepClassMember
            @Override
            public void onProfileLookupSucceeded(GameProfile profile) {
                uuid.set(profile.getId());
                if (uuid.get() == null) uuid.set(UUID.randomUUID());
            }

            @KeepClassMember
            @Override
            public void onProfileLookupFailed(GameProfile profile, Exception exception) {
                uuid.set(UUID.randomUUID());
            }
        });

        while (uuid.get() == null) Thread.onSpinWait();

        return uuid.get();
    }

    public static @NotNull String fixUrl(@NotNull String url) {
        if (URL_PATTERN.matcher(url).matches()) return url;
        else return "http://" + url;
    }
}
