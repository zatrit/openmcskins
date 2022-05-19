package net.zatrit.openmcskins.loader;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.resolvers.Resolver;
import net.zatrit.openmcskins.resolvers.handler.IndexedPlayerHandler;
import net.zatrit.openmcskins.util.PlayerManager;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class TextureLoader {
    public static void resolveSkin(GameProfile sourceProfile, SkinResolveCallback callback) {
        final List<? extends Resolver<?>> hosts = OpenMCSkins.getResolvers();
        final AtomicReference<Map<Type, IndexedPlayerHandler<?>>> leading = new AtomicReference<>(new HashMap<>());

        Flowable.range(0, hosts.size()).parallel().runOn(Schedulers.io()).mapOptional(i -> {
            GameProfile profile;

            if (hosts.get(i).requiresUUID()) profile = PlayerManager.patchProfile(sourceProfile);
            else profile = sourceProfile;

            try {
                Resolver<?> resolver = hosts.get(i);
                return Optional.of(resolver.resolvePlayer(profile).withIndex(i));
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
                PlayerManager.registerTextureId(identifier);
                callback.onSkinResolved(k, identifier, v.getModelOrDefault());
            });
        }).doOnError(OpenMCSkins::handleError).subscribe();
    }

    @FunctionalInterface
    public interface SkinResolveCallback {
        void onSkinResolved(Type type, @Nullable Identifier location, String model);
    }
}
