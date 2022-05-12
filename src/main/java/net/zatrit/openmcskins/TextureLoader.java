package net.zatrit.openmcskins;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.resolvers.Resolver;
import net.zatrit.openmcskins.resolvers.data.IndexedPlayerData;
import net.zatrit.openmcskins.util.PlayerSessionsManager;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class TextureLoader {
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
                PlayerSessionsManager.registerId(identifier);
                callback.onTextureResolved(k, identifier, v.getModelOrDefault());
            });
        }).doOnSubscribe(a -> profile.set(PlayerSessionsManager.getGameProfile(player))).doOnError(OpenMCSkins::handleError).subscribe();
    }

    public interface TextureResolveCallback {
        void onTextureResolved(Type type, @Nullable Identifier location, String model);
    }
}
