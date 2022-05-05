package net.zatrit.openmcskins;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilGameProfileRepository;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.resolvers.AbstractResolver;
import net.zatrit.openmcskins.util.NetworkUtils;
import net.zatrit.openmcskins.util.ObjectUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class TextureLoader {
    private static final Type[] supportedTypes = new Type[]{Type.CAPE, Type.SKIN};
    private final static YggdrasilMinecraftSessionService sessionService = (YggdrasilMinecraftSessionService) MinecraftClient.getInstance().getSessionService();
    private final static YggdrasilGameProfileRepository profileRepository = (YggdrasilGameProfileRepository) sessionService.getAuthenticationService().createProfileRepository();


    public static void resolve(PlayerListEntry player, TextureResolveCallback callback) {
        final List<? extends AbstractResolver<?>> hosts = OpenMCSkins.getResolvers();
        final AtomicReference<Map<Type, AbstractResolver.IndexedPlayerData<?>>> leading = new AtomicReference<>(new HashMap<>());

        Flowable.range(0, hosts.size()).parallel().runOn(Schedulers.io()).mapOptional(i -> {
            // Get PlayerData from resolver
            // If all resolved leading PlayerData's loaded, it won't try to load,
            // and it's makes texture loading process faster
            if (leading.get().values().stream().allMatch(x -> x.getIndex() < i) && leading.get().size() == 2)
                return Optional.empty();

            try {

                AbstractResolver<?> resolver = hosts.get(i);
                return Optional.of(resolver.resolvePlayer(getGameProfile(player)).withIndex(i));
            } catch (Exception ex) {
                OpenMCSkins.handleError(ex);
                return Optional.empty();
            }
        }).sequential().timeout(OpenMCSkins.getConfig().getResolvingTimeout(), TimeUnit.SECONDS).doOnEach(x -> {
            if (x.getValue() != null) for (Type t : supportedTypes) {
                if (!x.getValue().hasTexture(t)) continue;
                if (!leading.get().containsKey(t) || leading.get().get(t).getIndex() > x.getValue().getIndex())
                    leading.get().put(t, x.getValue());
            }
        }).doFinally(() -> {
            // The following code will be executed
            // When all textures are loaded
            // Or time out
            leading.get().forEach((k, v) -> callback.onTextureResolved(k, v.downloadTexture(k), v.getModelOrDefault()));
        }).doOnError(OpenMCSkins::handleError).subscribe();
    }

    public static MinecraftSessionService getSessionService() {
        return sessionService;
    }

    public interface TextureResolveCallback {
        void onTextureResolved(Type type, @Nullable Identifier location, String model);
    }

    private static GameProfile getGameProfile(@NotNull PlayerListEntry entry) {
        GameProfile profile = entry.getProfile();

        if (OpenMCSkins.getConfig().getOfflineMode()) {
            UUID id;
            UUID cachedId = OpenMCSkins.getUuidCache().getIfPresent(profile.getName());
            if (cachedId == null) {
                id = NetworkUtils.getUUIDByName(profileRepository, profile.getName());
                OpenMCSkins.getUuidCache().put(profile.getName(), id);
            } else id = cachedId;
            profile = ObjectUtils.setGameProfileUUID(profile, id);
        }

        return profile;
    }
}
