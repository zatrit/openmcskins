package net.zatrit.openmcskins;

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.resolvers.AbstractResolver;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public final class TextureLoader {
    private static final Type[] SUPPORTED_TYPES = new Type[]{Type.CAPE, Type.SKIN};

    public static void resolve(PlayerListEntry info, TextureResolveCallback callback) {
        final List<AbstractResolver<? extends AbstractResolver.IndexedPlayerData>> hosts = OpenMCSkins.getHosts();
        final AtomicReference<Map<Type, AbstractResolver.IndexedPlayerData>> leading = new AtomicReference<>(new HashMap<>());

        Flowable.range(0, hosts.size()).parallel().runOn(Schedulers.io()).mapOptional(i -> {
            // Get PlayerData from resolver
            try {
                AbstractResolver<? extends AbstractResolver.IndexedPlayerData> resolver = hosts.get(i);
                OpenMCSkins.LOGGER.info(String.format("[%s] Attempting to load skin", resolver.getName()));

                return Optional.of(resolver.resolvePlayer(info).withIndex(i));
            } catch (Exception ex) {
                handleError(ex);
                return Optional.empty();
            }
        }).sequential().timeout(OpenMCSkins.getConfig().resolvingTimeout, TimeUnit.SECONDS).doOnEach(x -> {
            if (x.getValue() != null) for (Type t : SUPPORTED_TYPES) {
                if (!x.getValue().hasTexture(t)) continue;
                if (!leading.get().containsKey(t) || leading.get().get(t).getIndex() > x.getValue().getIndex())
                    leading.get().put(t, x.getValue());
            }
        }).doFinally(() -> {
            // The following code will be executed
            // When all textures are loaded
            // Or time out
            leading.get().forEach((k, v) -> callback.onTextureResolved(k, v.downloadTexture(k), v.getModelOrDefault()));
        }).doOnError(TextureLoader::handleError).subscribe();
    }

    private static void handleError(@NotNull Throwable error) {
        error.printStackTrace();
    }

    public interface TextureResolveCallback {
        void onTextureResolved(Type type, Identifier location, String model);
    }
}
