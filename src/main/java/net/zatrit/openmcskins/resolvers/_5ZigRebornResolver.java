package net.zatrit.openmcskins.resolvers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.interfaces.resolver.Resolver;
import net.zatrit.openmcskins.resolvers.handler.AbstractPlayerHandler;
import net.zatrit.openmcskins.util.io.NetworkUtils;
import net.zatrit.openmcskins.util.io.TextureUtils;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class _5ZigRebornResolver implements Resolver<_5ZigRebornResolver.PlayerHandler> {
    @Override
    public PlayerHandler resolvePlayer(GameProfile profile) {
        return new PlayerHandler(profile);
    }

    public static class PlayerHandler extends AbstractPlayerHandler<String> {
        private static final String BASE_URL = "https://textures.5zigreborn.eu/profile/";

        public PlayerHandler(@NotNull GameProfile profile) {
            String url = BASE_URL + profile.getId();
            if (NetworkUtils.getResponseCode(url) != 200) return;

            this.textures.put(MinecraftProfileTexture.Type.CAPE, url);
        }

        @SuppressWarnings("unchecked")
        @Override
        public @Nullable Identifier downloadTexture(MinecraftProfileTexture.Type type) {
            try {
                String url = textures.get(type);
                InputStreamReader reader = new InputStreamReader(new URL(url).openStream(), StandardCharsets.UTF_8);
                Map<String, String> map = GSON.fromJson(reader, Map.class);
                String base64String = map.get("d");
                if (base64String == null) return null;
                byte[] bytes = Base64.decodeBase64(base64String);
                return TextureUtils.loadStaticTexture(() -> new ByteArrayInputStream(bytes), url, TextureUtils.getAspects(type), true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
