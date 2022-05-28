package net.zatrit.openmcskins.io.skins.loader;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import it.unimi.dsi.fastutil.ints.IntComparators;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.api.resolver.Resolver;
import net.zatrit.openmcskins.io.skins.Loaders;
import net.zatrit.openmcskins.io.skins.PlayerRegistry;
import net.zatrit.openmcskins.io.skins.resolvers.handler.AbstractPlayerHandler;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class VanillaLoader implements Loader {
    @Override
    public boolean filter(Resolver<?> resolver) {
        return true;
    }

    @Override
    public Object processHandlers(List<? extends AbstractPlayerHandler<?>> handlers) {
        Map<MinecraftProfileTexture.Type, AbstractPlayerHandler<?>> leading = new EnumMap<>(MinecraftProfileTexture.Type.class);

        for (MinecraftProfileTexture.Type type : MinecraftProfileTexture.Type.values()) {
            leading.put(type, handlers.stream()
                    .filter(x -> x.hasTexture(type))
                    .min((a, b) -> IntComparators.NATURAL_COMPARATOR.compare(a.getIndex(), b.getIndex()))
                    .orElse(null));
        }

        return leading;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void doFinally(Object result, GameProfile profile, Object @NotNull [] args) {
        Loaders.SkinResolveCallback callback = (Loaders.SkinResolveCallback) args[0];
        Map<MinecraftProfileTexture.Type, AbstractPlayerHandler<?>> leading = (Map<MinecraftProfileTexture.Type, AbstractPlayerHandler<?>>) result;

        leading.forEach((k, v) -> {
            try {
                Identifier identifier = v.downloadTexture(k);
                PlayerRegistry.registerTextureId(identifier);
                callback.onSkinResolved(k, identifier, v.getModel());
            } catch (Exception ignored) {
            }
        });
    }
}
