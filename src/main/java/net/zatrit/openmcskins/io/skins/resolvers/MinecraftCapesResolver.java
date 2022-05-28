package net.zatrit.openmcskins.io.skins.resolvers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.zatrit.openmcskins.api.resolver.Resolver;
import net.zatrit.openmcskins.io.skins.resolvers.handler.AnimatedPlayerHandler;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

public class MinecraftCapesResolver implements Resolver<MinecraftCapesResolver.PlayerHandler> {
    @Override
    public PlayerHandler resolvePlayer(GameProfile profile) throws IOException {
        return new PlayerHandler(profile);
    }

    public static class PlayerHandler extends AnimatedPlayerHandler {
        private static final String BASE_URL = "https://minecraftcapes.net/profile/";
        private final Map<String, ?> data;

        @SuppressWarnings("unchecked")
        public PlayerHandler(@NotNull GameProfile profile) throws IOException {
            String url = BASE_URL + profile.getId().toString().replace("-", "");

            data = GSON.<Map<String, ?>>fromJson(new InputStreamReader(new URL(url).openStream()), Map.class);

            Map<String, String> textures = (Map<String, String>) data.get("textures");
            Arrays.stream(MinecraftProfileTexture.Type.values()).parallel().forEach(t -> {
                String k = t.toString().toLowerCase();
                if (textures.containsKey(k) && textures.get(k) != null) this.textures.put(t, textures.get(k));
            });
        }

        @Override
        protected InputStream openStream(String data, MinecraftProfileTexture.@NotNull Type type) {
            byte[] bytes = Base64.decodeBase64(data);
            return new ByteArrayInputStream(bytes);
        }

        @Override
        protected boolean isAnimated(MinecraftProfileTexture.Type type) {
            return type == MinecraftProfileTexture.Type.CAPE && (boolean) data.get("animatedCape");
        }
    }
}
