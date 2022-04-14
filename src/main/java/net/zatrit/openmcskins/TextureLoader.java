package net.zatrit.openmcskins;

import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.resources.ResourceLocation;
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

    public static void resolve(PlayerInfo info, TextureResolveCallback callback) {
        final List<AbstractResolver<? extends AbstractResolver.PlayerData>> hosts = OpenMCSkins.HOSTS;
        final AtomicReference<Map<Type, AbstractResolver.PlayerData>> leading = new AtomicReference<>(new HashMap<>());

        Flowable.range(0, hosts.size()).parallel().runOn(Schedulers.io()).mapOptional(i -> {
            // Get PlayerData from resolver
            try {
                AbstractResolver<? extends AbstractResolver.PlayerData> resolver = hosts.get(i);
                OpenMCSkins.LOGGER.info(String.format("[%s] Attempting to load skin", resolver.getName()));

                AbstractResolver.PlayerData resolvedData = resolver.resolvePlayer(info);
                resolvedData.index = i;
                return Optional.of(resolvedData);
            } catch (Exception ex) {
                handleError(ex);
                return Optional.empty();
            }
        }).sequential().timeout(OpenMCSkins.CONFIG_FILE.resolvingTimeout, TimeUnit.SECONDS).doOnEach(x -> {
            if (x.getValue() != null) for (int i = 0; i < SUPPORTED_TYPES.length; i = i + 1) {
                Type t = SUPPORTED_TYPES[i];
                if (!x.getValue().hasTexture(t)) continue;
                if (!leading.get().containsKey(t) || leading.get().get(t).index > x.getValue().index)
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
        void onTextureResolved(Type type, ResourceLocation location, String model);
    }
}
