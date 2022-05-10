package net.zatrit.openmcskins;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilGameProfileRepository;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.resolvers.Resolver;
import net.zatrit.openmcskins.resolvers.data.IndexedPlayerData;
import net.zatrit.openmcskins.util.NetworkUtils;
import net.zatrit.openmcskins.util.textures.AnimatedTexture;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class TextureLoader {
    private final static YggdrasilMinecraftSessionService sessionService = (YggdrasilMinecraftSessionService) MinecraftClient.getInstance().getSessionService();
    private final static YggdrasilGameProfileRepository profileRepository = (YggdrasilGameProfileRepository) sessionService.getAuthenticationService().createProfileRepository();
    private static final Cache<String, UUID> uuidCache = CacheBuilder.newBuilder().build();
    private static final ArrayList<Identifier> idRegistry = new ArrayList<>();

    public static void resolve(PlayerListEntry player, TextureResolveCallback callback) {
        final List<? extends Resolver<?>> hosts = OpenMCSkins.getResolvers();
        final AtomicReference<Map<Type, IndexedPlayerData<?>>> leading = new AtomicReference<>(new HashMap<>());
        final AtomicReference<GameProfile> profile = new AtomicReference<>(null);

        Flowable.range(0, hosts.size()).parallel().runOn(Schedulers.io()).mapOptional(i -> {
            // Get PlayerData from resolver
            // If all resolved leading PlayerData's loaded, it won't try to load,
            // and it's makes texture loading process faster
            while (profile.get() == null) Thread.onSpinWait();

            try {
                Resolver<?> resolver = hosts.get(i);
                return Optional.of(resolver.resolvePlayer(profile.get()).withIndex(i));
            } catch (Exception ex) {
                OpenMCSkins.handleError(ex);
                return Optional.empty();
            }
        }).sequential().timeout(OpenMCSkins.getConfig().resolvingTimeout, TimeUnit.SECONDS).doOnEach(x -> {
            if (x.getValue() != null) for (Type t : Type.values()) {
                if (!x.getValue().hasTexture(t)) continue;
                if (!leading.get().containsKey(t) || leading.get().get(t).getIndex() > x.getValue().getIndex())
                    leading.get().put(t, x.getValue());
            }
        }).doFinally(() -> {
            // The following code will be executed
            // When all textures are loaded
            // Or time out
            leading.get().forEach((k, v) -> {
                Identifier identifier = v.downloadTexture(k);
                registerId(identifier);
                callback.onTextureResolved(k, identifier, v.getModelOrDefault());
            });
        }).doOnSubscribe(a -> profile.set(getGameProfile(player))).doOnError(OpenMCSkins::handleError).subscribe();
    }

    public static MinecraftSessionService getSessionService() {
        return sessionService;
    }

    private static GameProfile getGameProfile(@NotNull PlayerListEntry entry) {
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

    public interface TextureResolveCallback {
        void onTextureResolved(Type type, @Nullable Identifier location, String model);
    }
}
