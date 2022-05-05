package net.zatrit.openmcskins.resolvers.capes;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.zatrit.openmcskins.resolvers.AbstractResolver;
import net.zatrit.openmcskins.util.NetworkUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class OptifineCapeResolver extends AbstractResolver<OptifineCapeResolver.PlayerData> {
    public OptifineCapeResolver() {

    }

    @Override
    public PlayerData resolvePlayer(@NotNull GameProfile profile) {
        return new PlayerData(profile.getName());
    }

    public static class PlayerData extends MinecraftProfileTexturePlayerData {
        private static final String BASE_URL = "http://s.optifine.net/capes/%s.png";

        public PlayerData(String name) {
            String formattedUrl = String.format(BASE_URL, name);

            if (NetworkUtils.getResponseCode(formattedUrl) != 200) return;

            MinecraftProfileTexture texture = new MinecraftProfileTexture(formattedUrl, new HashMap<>());
            textures.put(MinecraftProfileTexture.Type.CAPE, texture);
        }
    }
}
