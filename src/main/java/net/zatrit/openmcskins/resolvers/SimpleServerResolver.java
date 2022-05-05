package net.zatrit.openmcskins.resolvers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static net.zatrit.openmcskins.util.ObjectUtils.getOfDefaultNonGeneric;

public class SimpleServerResolver extends AbstractResolver<SimpleServerResolver.PlayerData> {
    protected final String host;

    public SimpleServerResolver(String host) {
        this.host = host;
    }

    @Override
    public PlayerData resolvePlayer(@NotNull GameProfile profile) throws IOException {
        // Example: http://127.0.0.1:8080/textures/PlayerName
        final String url = String.format("%s/textures/%s", host, profile.getName());
        return fetchData(url);
    }

    private @NotNull SimpleServerResolver.PlayerData fetchData(String url) throws IOException {
        URL realUrl = new URL(url);
        BufferedReader in = new BufferedReader(new InputStreamReader(realUrl.openStream()));
        Map<String, Map<String, ?>> map = GSON.<Map<String, Map<String, ?>>>fromJson(in, Map.class);
        return new PlayerData(map);
    }

    public static class PlayerData extends MinecraftProfileTexturePlayerData {

        @SuppressWarnings("unchecked")
        public PlayerData(@NotNull Map<String, Map<String, ?>> data) {
            data.forEach((k, v) -> {
                MinecraftProfileTexture.Type type = MinecraftProfileTexture.Type.valueOf(k);
                Map<String, String> metadata = (Map<String, String>) getOfDefaultNonGeneric(v, "metadata", new HashMap<>());
                MinecraftProfileTexture texture = new MinecraftProfileTexture((String) v.get("url"), metadata);
                this.textures.put(type, texture);
                if (metadata.containsKey("model")) this.setModel(metadata.get("model"));
            });
        }
    }
}
