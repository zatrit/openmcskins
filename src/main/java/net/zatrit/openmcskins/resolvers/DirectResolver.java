package net.zatrit.openmcskins.resolvers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.zatrit.openmcskins.util.NetworkUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class DirectResolver extends AbstractResolver<DirectResolver.PlayerData> {
    private final String baseUrl;
    private final MinecraftProfileTexture.Type type;

    public DirectResolver(String baseUrl, MinecraftProfileTexture.Type type) {
        this.baseUrl = baseUrl;
        this.type = type;
    }

    @Override
    public PlayerData resolvePlayer(GameProfile profile) {
        return new PlayerData(baseUrl, profile, type);
    }

    public static class PlayerData extends MinecraftProfileTexturePlayerData {
        public PlayerData(String baseUrl, @NotNull GameProfile profile, MinecraftProfileTexture.Type type) {
            String formattedUrl = baseUrl.replace("{name}", profile.getName()).replace("{id}", profile.getId().toString());

            if (NetworkUtils.getResponseCode(formattedUrl) != 200) return;

            MinecraftProfileTexture texture = new MinecraftProfileTexture(formattedUrl, new HashMap<>());
            textures.put(type, texture);
        }
    }
}
