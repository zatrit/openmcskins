package net.zatrit.openmcskins.io.skins;

import com.google.gson.internal.LinkedTreeMap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.api.resolver.Resolver;
import net.zatrit.openmcskins.render.textures.AnimatedTexture;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public final class PlayerRegistry {
    private final static YggdrasilMinecraftSessionService sessionService = (YggdrasilMinecraftSessionService) MinecraftClient.getInstance().getSessionService();
    private static final Map<String, GameProfile> profileCache = new HashMap<>();
    private static final ArrayList<Identifier> idRegistry = new ArrayList<>();

    private PlayerRegistry() {
    }

    public static YggdrasilMinecraftSessionService getSessionService() {
        return sessionService;
    }

    public static GameProfile patchProfile(@NotNull GameProfile profile) {
        if (OpenMCSkins.getConfig().uuidResolutionMode.shouldResolveByName()) {
            synchronized (profile) {
                try {
                    return profileCache.computeIfAbsent(profile.getName(), k -> {
                        try {
                            GameProfile cachedProfile;

                            final URL url;
                            url = new URL("https://api.mojang.com/users/profiles/minecraft/" + profile.getName());
                            final LinkedTreeMap<String, Object> map = Resolver.GSON.<LinkedTreeMap<String, Object>>fromJson(
                                    new InputStreamReader(url.openStream()),
                                    LinkedTreeMap.class);
                            // https://stackoverflow.com/a/18987428/12245612
                            UUID id = UUID.fromString(String.valueOf(map.get("id")).replaceAll(
                                    "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                                    "$1-$2-$3-$4-$5"));
                            cachedProfile = new GameProfile(id, profile.getName());
                            cachedProfile.getProperties().putAll(profile.getProperties());

                            return cachedProfile;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (Exception ex) {
                    OpenMCSkins.handleError(ex);
                }
            }
        }
        return profile;
    }

    public static void registerTextureId(Identifier identifier) {
        idRegistry.add(identifier);
    }

    public static synchronized void clear() {
        final TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
        idRegistry.stream().parallel().filter(Objects::nonNull).filter(x -> textureManager.getTexture(x) instanceof AnimatedTexture).forEach(
                x -> textureManager.getTexture(x).close());
        idRegistry.clear();
        profileCache.clear();
    }
}
