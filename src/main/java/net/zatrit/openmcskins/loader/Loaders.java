package net.zatrit.openmcskins.loader;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import it.unimi.dsi.fastutil.ints.IntComparators;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.resolvers.PlayerCosmeticsResolver;
import net.zatrit.openmcskins.resolvers.Resolver;
import net.zatrit.openmcskins.resolvers.handler.PlayerCosmeticsHandler;
import net.zatrit.openmcskins.resolvers.handler.PlayerHandler;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public enum Loaders {
    COSMETICS(x -> x instanceof PlayerCosmeticsResolver<?>, handlers -> {
        // Operating with handlers list
        List<Cosmetics.CosmeticsItem> allCosmetics = new ArrayList<>();

        handlers.stream().map(x -> ((PlayerCosmeticsHandler) x).downloadCosmetics()).forEach(cosmetics -> {
            if (cosmetics != null) allCosmetics.addAll(cosmetics);
        });

        return allCosmetics;
    }, (data, callback, profile) -> {
        // Do finally
        List<Cosmetics.CosmeticsItem> cosmetics = (List<Cosmetics.CosmeticsItem>) data;
        Cosmetics.PLAYER_COSMETICS.put(profile.getName(), cosmetics);
    }),
    VANILLA(x -> true, handlers -> {
        // Operating with handlers list
        Map<Type, PlayerHandler<?>> leading = new EnumMap<>(Type.class);

        for (Type type : Type.values()) {
            leading.put(type, handlers.stream()
                    .filter(x -> x.hasTexture(type))
                    .min((a, b) -> IntComparators.NATURAL_COMPARATOR.compare(a.getIndex(), b.getIndex()))
                    .orElse(null));
        }

        return leading;
    }, (a, b, profile) -> {
        // Do finally
        SkinResolveCallback callback = (SkinResolveCallback) b;
        Map<Type, PlayerHandler<?>> leading = (Map<Type, PlayerHandler<?>>) a;

        leading.forEach((k, v) -> {
            try {
                Identifier identifier = v.downloadTexture(k);
                PlayerManager.registerTextureId(identifier);
                callback.onSkinResolved(k, identifier, v.getModelOrDefault());
            } catch (Exception ignore) {
            }
        });
    });
    private final AsyncLoader loader;

    Loaders(Function<Resolver<?>, Boolean> filter, Function<List<? extends PlayerHandler<?>>, ?> processHandlers, TriConsumer<Object, Object, GameProfile> doFinally) {
        this.loader = new AsyncLoader(filter, processHandlers, doFinally);
    }

    @Contract(value = " -> new", pure = true)
    public @NotNull AsyncLoader getLoader() {
        return this.loader;
    }


    @FunctionalInterface
    public interface SkinResolveCallback {
        void onSkinResolved(Type type, @Nullable Identifier location, String model);
    }
}
