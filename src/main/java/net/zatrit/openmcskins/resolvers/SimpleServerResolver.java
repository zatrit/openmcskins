package net.zatrit.openmcskins.resolvers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.zatrit.openmcskins.resolvers.data.AnimatedPlayerData;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static net.zatrit.openmcskins.util.CollectionUtils.getOfDefaultNonGeneric;

public record SimpleServerResolver(String host) implements Resolver<SimpleServerResolver.PlayerData> {
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

    public static class PlayerData extends AnimatedPlayerData {
        @SuppressWarnings("unchecked")
        public PlayerData(Map<String, Map<String, ?>> data) {
            if (data != null) data.forEach((k, v) -> {
                MinecraftProfileTexture.Type type = MinecraftProfileTexture.Type.valueOf(k);
                Map<String, ?> metadata = (Map<String, ?>) getOfDefaultNonGeneric(v, "metadata", new HashMap<>());

                this.textures.put(type, (String) v.get("url"));

                if (metadata.containsKey("model")) this.setModel((String) metadata.get("model"));
                if (metadata.containsKey("animated")) this.setAnimated(type, (boolean) metadata.get("animated"));

                metadata.clear();
            });
        }
    }
}
