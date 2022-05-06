package net.zatrit.openmcskins.resolvers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.util.NetworkUtils;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class _5ZigRebornResolver extends AbstractResolver<_5ZigRebornResolver.PlayerData> {
    @Override
    public PlayerData resolvePlayer(GameProfile profile) {
        return new PlayerData(profile);
    }

    public static class PlayerData extends IndexedPlayerData<String> {
        private static final String BASE_URL = "https://textures.5zigreborn.eu/profile/";

        public PlayerData(@NotNull GameProfile profile) {
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
                return NetworkUtils.loadStaticCape(new ByteArrayInputStream(bytes), url);
            } catch (Exception e) {
                OpenMCSkins.handleError(e);
                return null;
            }
        }
    }
}
