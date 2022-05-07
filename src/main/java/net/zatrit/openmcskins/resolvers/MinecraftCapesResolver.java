package net.zatrit.openmcskins.resolvers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.zatrit.openmcskins.resolvers.data.AnimatedPlayerData;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

public class MinecraftCapesResolver implements Resolver<MinecraftCapesResolver.PlayerData> {
    @Override
    public PlayerData resolvePlayer(GameProfile profile) throws IOException {
        return new PlayerData(profile);
    }

    public static class PlayerData extends AnimatedPlayerData {
        private static final String BASE_URL = "https://minecraftcapes.net/profile/";
        private final Map<String, ?> data;

        @SuppressWarnings("unchecked")
        public PlayerData(@NotNull GameProfile profile) throws IOException {
            String url = BASE_URL + profile.getId().toString().replace("-", "");

            data = GSON.<Map<String, ?>>fromJson(new InputStreamReader(new URL(url).openStream()), Map.class);

            Map<String, String> textures = (Map<String, String>) data.get("textures");
            Arrays.stream(MinecraftProfileTexture.Type.values()).forEach(t -> {
                String k = t.toString().toLowerCase();
                if (textures.containsKey(k) && textures.get(k) != null) this.textures.put(t, url);
            });
        }

        @SuppressWarnings("unchecked")
        @Override
        protected InputStream openStream(String path, MinecraftProfileTexture.@NotNull Type type) {
            Map<String, String> textures = (Map<String, String>) data.get("textures");

            if (textures.get(type.toString().toLowerCase()) == null) return null;
            byte[] bytes = Base64.decodeBase64(textures.get(type.toString().toLowerCase()));
            return new ByteArrayInputStream(bytes);
        }

        @Override
        protected boolean isAnimated(MinecraftProfileTexture.Type type) {
            return type == MinecraftProfileTexture.Type.CAPE && (boolean) data.get("animatedCape");
        }

        @Override
        public boolean hasTexture(MinecraftProfileTexture.Type type) {
            return this.textures.containsKey(type);
        }
    }
}
