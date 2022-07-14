package net.zatrit.openmcskins.io.skins.resolvers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.api.resolver.Resolver;
import net.zatrit.openmcskins.io.skins.resolvers.handler.AnimatedPlayerHandler;
import net.zatrit.openmcskins.io.util.NetworkUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public record DirectResolver(String baseUrl,
                             MinecraftProfileTexture.Type type) implements Resolver<DirectResolver.PlayerHandler> {
    public DirectResolver(String baseUrl, MinecraftProfileTexture.Type type) {
        this.baseUrl = NetworkUtils.fixUrl(baseUrl);
        this.type = type;
    }

    @Override
    public boolean requiresUUID() {
        return baseUrl.contains("{id}");
    }

    @Contract("_ -> new")
    @Override
    public @NotNull DirectResolver.PlayerHandler resolvePlayer(GameProfile profile) {
        return new PlayerHandler(baseUrl, profile, type);
    }

    public static class PlayerHandler extends AnimatedPlayerHandler {
        public PlayerHandler(@NotNull String baseUrl, @NotNull GameProfile profile, MinecraftProfileTexture.Type type) {
            final String formattedUrl = baseUrl.replace("{name}", profile.getName()).replace("{id}", profile.getId().toString());

            try {
                if (NetworkUtils.getResponseCode(formattedUrl) == 200) textures.put(type, formattedUrl);
            } catch (IOException e) {
                OpenMCSkins.handleError(e);
            }
        }
    }
}
