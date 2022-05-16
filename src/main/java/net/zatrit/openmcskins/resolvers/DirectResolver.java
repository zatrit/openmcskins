package net.zatrit.openmcskins.resolvers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.zatrit.openmcskins.resolvers.handler.AnimatedTexturePlayerHandler;
import net.zatrit.openmcskins.util.io.NetworkUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record DirectResolver(String baseUrl,
                             MinecraftProfileTexture.Type type) implements Resolver<DirectResolver.PlayerHandler> {
    public DirectResolver(String baseUrl,
                          MinecraftProfileTexture.Type type) {
        this.baseUrl = NetworkUtils.fixUrl(baseUrl);
        this.type = type;
    }

    @Contract("_ -> new")
    @Override
    public @NotNull DirectResolver.PlayerHandler resolvePlayer(GameProfile profile) {
        return new PlayerHandler(baseUrl, profile, type);
    }

    public static class PlayerHandler extends AnimatedTexturePlayerHandler {
        public PlayerHandler(@NotNull String baseUrl, @NotNull GameProfile profile, MinecraftProfileTexture.Type type) {
            String formattedUrl = baseUrl.replace("{name}", profile.getName()).replace("{id}", profile.getId().toString());

            if (NetworkUtils.getResponseCode(formattedUrl) != 200) return;
            textures.put(type, formattedUrl);
        }
    }
}
