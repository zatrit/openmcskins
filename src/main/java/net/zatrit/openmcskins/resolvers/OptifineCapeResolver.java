package net.zatrit.openmcskins.resolvers;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.network.PlayerListEntry;
import net.zatrit.openmcskins.util.NetworkUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class OptifineCapeResolver extends AbstractResolver<OptifineCapeResolver.PlayerData> {
    public OptifineCapeResolver() {

    }

    @Override
    public PlayerData resolvePlayer(@NotNull PlayerListEntry player) {
        return new PlayerData(player.getProfile().getName());
    }

    public static class PlayerData extends MinecraftProfilePlayerData {
        private static final String URL = "http://s.optifine.net/capes/%s.png";

        public PlayerData(String name) {
            String formattedUrl = String.format(URL, name);

            if (NetworkUtils.getResponseCode(formattedUrl) != 200) return;

            MinecraftProfileTexture texture = new MinecraftProfileTexture(formattedUrl, new HashMap<>());
            textures.put(MinecraftProfileTexture.Type.CAPE, texture);
        }
    }
}
