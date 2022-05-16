package net.zatrit.openmcskins.loader;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.resolvers.PlayerCosmeticsResolver;
import net.zatrit.openmcskins.resolvers.Resolver;
import net.zatrit.openmcskins.resolvers.handler.IndexedPlayerHandler;
import net.zatrit.openmcskins.resolvers.handler.PlayerCosmeticsHandler;
import net.zatrit.openmcskins.util.PlayerSessionsManager;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class TextureLoader {
    public static void resolveSkin(PlayerListEntry player, SkinResolveCallback callback) {
        final List<? extends Resolver<?>> hosts = OpenMCSkins.getResolvers();
        final AtomicReference<Map<Type, IndexedPlayerHandler<?>>> leading = new AtomicReference<>(new HashMap<>());
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
                PlayerSessionsManager.registerId(identifier);
                callback.onSkinResolved(k, identifier, v.getModelOrDefault());
            });
        }).doOnSubscribe(a -> profile.set(PlayerSessionsManager.getGameProfile(player))).doOnError(OpenMCSkins::handleError).subscribe();
    }

    public static void resolveCosmetics(PlayerListEntry player) {
        final List<? extends Resolver<?>> hosts = OpenMCSkins.getResolvers();
        final AtomicReference<GameProfile> profile = new AtomicReference<>(player.getProfile());
        final List<CosmeticsLoader.CosmeticsItem> cosmetics = new ArrayList<>();
        CosmeticsLoader.COSMETICS.put(profile.get().getName(), cosmetics);

        Flowable.range(0, hosts.size()).parallel().runOn(Schedulers.io()).doOnNext(i -> {
            while (profile.get() == null) Thread.onSpinWait();

            if (hosts.get(i) instanceof PlayerCosmeticsResolver)
                try {
                    IndexedPlayerHandler<?> data = hosts.get(i).resolvePlayer(profile.get());
                    cosmetics.addAll(Objects.requireNonNull(((PlayerCosmeticsHandler) data).downloadCosmetics()));
                } catch (NullPointerException ex) {
                    OpenMCSkins.handleError(ex);
                }
        }).sequential().timeout(OpenMCSkins.getConfig().resolvingTimeout, TimeUnit.SECONDS).doFinally(() -> {
            if (cosmetics.size() > 0) CosmeticsLoader.COSMETICS.put(profile.get().getName(), cosmetics);
        }).doOnError(OpenMCSkins::handleError).subscribe();
    }

    @FunctionalInterface
    public interface SkinResolveCallback {
        void onSkinResolved(Type type, @Nullable Identifier location, String model);
    }
}
