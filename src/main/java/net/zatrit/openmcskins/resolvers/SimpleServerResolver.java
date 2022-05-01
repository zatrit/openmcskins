package net.zatrit.openmcskins.resolvers;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.network.PlayerListEntry;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static net.zatrit.openmcskins.util.ObjectUtils.getOfDefaultNonGeneric;

public class SimpleServerResolver extends AbstractResolver<SimpleServerResolver.IndexedPlayerData> {
    protected final String host;

    public SimpleServerResolver(String host) {
        this.host = host;
    }

    @Override
    public IndexedPlayerData resolvePlayer(@NotNull PlayerListEntry info) throws IOException {
        // Example: http://127.0.0.1:8080/api/userdata/PlayerName
        final String url = String.format("%s/textures/%s", host, info.getProfile().getName());
        return fetchData(url);
    }

    private @NotNull SimpleServerResolver.IndexedPlayerData fetchData(String url) throws IOException {
        URL realUrl = new URL(url);
        BufferedReader in = new BufferedReader(new InputStreamReader(realUrl.openStream()));
        Map<String, Map<String, ?>> map = GSON.<Map<String, Map<String, ?>>>fromJson(in, Map.class);
        return new IndexedPlayerData(map);
    }

    public static class IndexedPlayerData extends AbstractURLPlayerData {

        @SuppressWarnings("unchecked")
        public IndexedPlayerData(@NotNull Map<String, Map<String, ?>> data) {
            data.forEach((k, v) -> {
                MinecraftProfileTexture.Type type = MinecraftProfileTexture.Type.valueOf(k);
                Map<String, String> metadata = (Map<String, String>) getOfDefaultNonGeneric(v, "metadata", new HashMap<>());
                MinecraftProfileTexture texture = new MinecraftProfileTexture((String) v.get("url"), metadata);
                this.textures.put(type, texture);
                if (metadata.containsKey("model")) this.model = metadata.get("model");
            });
        }
    }
}
