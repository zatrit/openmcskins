package net.zatrit.openmcskins.io.skins.resolvers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.api.resolver.Resolver;
import net.zatrit.openmcskins.io.skins.resolvers.handler.AbstractPlayerHandler;
import net.zatrit.openmcskins.io.util.NetworkUtils;
import net.zatrit.openmcskins.io.util.TextureUtils;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class _5ZigRebornResolver implements Resolver<_5ZigRebornResolver.PlayerHandler> {
    @Override
    public PlayerHandler resolvePlayer(GameProfile profile) {
        try {
            return new PlayerHandler(profile);
        } catch (IOException e) {
            OpenMCSkins.handleError(e);
            return null;
        }
    }

    public static class PlayerHandler extends AbstractPlayerHandler<String> {
        private static final String BASE_URL = "https://textures.5zigreborn.eu/profile/";

        public PlayerHandler(@NotNull GameProfile profile) throws IOException {
            String url = BASE_URL + profile.getId();
            if (NetworkUtils.getResponseCode(url) == 200) this.textures.put(MinecraftProfileTexture.Type.CAPE, url);
        }

        @SuppressWarnings("unchecked")
        @Override
        public @Nullable Identifier downloadTexture(MinecraftProfileTexture.Type type) {
            try {
                final String url = textures.get(type);
                final InputStreamReader reader = new InputStreamReader(new URL(url).openStream(), StandardCharsets.UTF_8);
                final Map<String, String> map = GSON.fromJson(reader, Map.class);
                final String base64String = map.get("d");
                if (base64String == null) return null;
                final byte[] bytes = Base64.decodeBase64(base64String);
                return TextureUtils.loadStaticTexture(() -> new ByteArrayInputStream(bytes), url, TextureUtils.getAspects(type), true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
