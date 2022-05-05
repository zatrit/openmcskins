package net.zatrit.openmcskins.resolvers.capes;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.resolvers.AbstractResolver;
import net.zatrit.openmcskins.util.NetworkUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

public class LabyModResolver extends AbstractResolver<LabyModResolver.PlayerData> {
    @Override
    public PlayerData resolvePlayer(GameProfile profile) {
        return new PlayerData(profile);
    }

    public static class PlayerData extends IndexedPlayerData<String> {
        private static final String BASE_URL = "https://dl.labymod.net/capes/";

        public PlayerData(@NotNull GameProfile profile) {
            String url = BASE_URL + profile.getId();
            if (NetworkUtils.getResponseCode(url) != 200) return;

            this.textures.put(MinecraftProfileTexture.Type.CAPE, url);
        }

        @Override
        public @Nullable Identifier downloadTexture(MinecraftProfileTexture.Type type) {
            try {
                String url = textures.get(type);
                return NetworkUtils.downloadAndResize(new URL(url).openStream(), url, 2, 1);
            } catch (Exception e) {
                OpenMCSkins.handleError(e);
                return null;
            }
        }
    }
}
