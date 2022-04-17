package net.zatrit.openmcskins.resolvers;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

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

    @Override
    public String getName() {
        return host;
    }

    private @NotNull SimpleServerResolver.IndexedPlayerData fetchData(String url) throws IOException {
        URL realUrl = new URL(url);
        BufferedReader in = new BufferedReader(new InputStreamReader(realUrl.openStream()));
        Map<String, Map<String, ?>> map = GSON.<Map<String, Map<String, ?>>>fromJson(in, Map.class);
        return new IndexedPlayerData(map);
    }

    public String getHost() {
        return this.host;
    }

    public static class IndexedPlayerData extends AbstractResolver.IndexedPlayerData<MinecraftProfileTexture> {

        public IndexedPlayerData(@NotNull Map<String, Map<String, ?>> data) {
            data.forEach((k, v) -> {
                Map<String, String> metadata = firstNonNull((Map<String, String>) v.get("metadata"), new HashMap<>());
                MinecraftProfileTexture texture = new MinecraftProfileTexture((String) v.get("url"), metadata);
                this.textures.put(MinecraftProfileTexture.Type.valueOf(k), texture);
                this.model = metadata.getOrDefault("model", "default");
            });
        }

        @Override
        public Identifier downloadTexture(MinecraftProfileTexture.Type type) {
            MinecraftProfileTexture texture = this.textures.get(type);
            return MinecraftClient.getInstance().getSkinProvider().loadSkin(texture, MinecraftProfileTexture.Type.CAPE);
        }
    }
}
