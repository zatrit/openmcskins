package net.zatrit.openmcskins.resolvers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.zatrit.openmcskins.resolvers.data.AnimatedPlayerData;
import net.zatrit.openmcskins.util.NetworkUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record DirectResolver(String baseUrl,
                             MinecraftProfileTexture.Type type) implements Resolver<DirectResolver.PlayerData> {
    @Contract("_ -> new")
    @Override
    public @NotNull PlayerData resolvePlayer(GameProfile profile) {
        return new PlayerData(baseUrl, profile, type);
    }

    public static class PlayerData extends AnimatedPlayerData {
        public PlayerData(@NotNull String baseUrl, @NotNull GameProfile profile, MinecraftProfileTexture.Type type) {
            String formattedUrl = baseUrl.replace("{name}", profile.getName()).replace("{id}", profile.getId().toString());

            if (NetworkUtils.getResponseCode(formattedUrl) != 200) return;
            textures.put(type, formattedUrl);
        }
    }
}
