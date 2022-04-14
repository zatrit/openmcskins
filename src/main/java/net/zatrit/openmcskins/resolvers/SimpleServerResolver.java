package net.zatrit.openmcskins.resolvers;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class SimpleServerResolver extends AbstractResolver<SimpleServerResolver.PlayerData> {
    protected final String host;

    @Override
    public PlayerData resolvePlayer(@NotNull PlayerInfo info) throws IOException {
        // Example: http://127.0.0.1:8080/api/userdata/PlayerName
        final String url = String.format("%s/textures/%s", host, info.getProfile().getName());
        PlayerData playerData = fetchData(url);
        return playerData;
    }

    @Override
    public String getName() {
        return host;
    }

    private @NotNull PlayerData fetchData(String url) throws IOException {
        URL realUrl = new URL(url);
        BufferedReader in = new BufferedReader(new InputStreamReader(realUrl.openStream()));
        Map<String, Map<String, ?>> map = GSON.<Map<String, Map<String, ?>>>fromJson(in, Map.class);
        return new PlayerData(map);
    }

    public SimpleServerResolver(String host) {
        this.host = host;
    }

    public String getHost() {
        return this.host;
    }

    public class PlayerData extends AbstractResolver.PlayerData {
        private final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures = new HashMap<>();

        @Override
        public ResourceLocation downloadTexture(MinecraftProfileTexture.Type type) {
            MinecraftProfileTexture texture = this.textures.get(type);
            return Minecraft.getInstance().getSkinManager().registerTexture(texture, MinecraftProfileTexture.Type.CAPE);
        }

        @Override
        public boolean hasTexture(MinecraftProfileTexture.Type type) {
            return this.textures.containsKey(type);
        }

        public PlayerData(@NotNull Map<String, Map<String, ?>> data) {
            data.forEach((k, v) -> {
                Map<String, String> metadata = firstNonNull((Map<String, String>) v.get("metadata"), new HashMap<>());
                MinecraftProfileTexture texture = new MinecraftProfileTexture((String) v.get("url"), metadata);
                this.textures.put(MinecraftProfileTexture.Type.valueOf(k), texture);
                this.model = metadata.getOrDefault("model", "default");
            });
        }
    }
}
