package net.zatrit.openmcskins.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilGameProfileRepository;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.render.textures.AnimatedTexture;
import net.zatrit.openmcskins.util.io.NetworkUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public final class PlayerSessionsManager {
    private final static YggdrasilMinecraftSessionService sessionService = (YggdrasilMinecraftSessionService) MinecraftClient.getInstance().getSessionService();
    private final static YggdrasilGameProfileRepository profileRepository = (YggdrasilGameProfileRepository) sessionService.getAuthenticationService().createProfileRepository();
    private static final Cache<String, UUID> uuidCache = CacheBuilder.newBuilder().build();
    private static final ArrayList<Identifier> idRegistry = new ArrayList<>();

    private PlayerSessionsManager() {
    }

    public static MinecraftSessionService getSessionService() {
        return sessionService;
    }

    public static GameProfile getGameProfile(@NotNull PlayerListEntry entry) {
        GameProfile profile = entry.getProfile();

        if (OpenMCSkins.getConfig().offlineMode) {
            UUID id;
            UUID cachedId = getUuidCache().getIfPresent(profile.getName());
            if (cachedId == null) {
                id = NetworkUtils.getUUIDByName(profileRepository, profile.getName());
                getUuidCache().put(profile.getName(), id);
            } else id = cachedId;

            profile = new GameProfile(id, profile.getName());
            profile.getProperties().putAll(profile.getProperties());
            return profile;
        }

        return profile;
    }

    @Contract(pure = true)
    public static @NotNull Cache<String, UUID> getUuidCache() {
        return uuidCache;
    }

    public static void registerId(Identifier identifier) {
        idRegistry.add(identifier);
    }

    public static void clearTextures() {
        TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
        idRegistry.stream().parallel().filter(Objects::nonNull).filter(x -> textureManager.getTexture(x) instanceof AnimatedTexture).forEach(x -> textureManager.getTexture(x).close());
        idRegistry.clear();
    }
}
