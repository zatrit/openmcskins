package net.zatrit.openmcskins.io.skins.loader;

import com.mojang.authlib.GameProfile;
import net.zatrit.openmcskins.api.handler.PlayerCosmeticsHandler;
import net.zatrit.openmcskins.api.resolver.CosmeticsResolver;
import net.zatrit.openmcskins.api.resolver.Resolver;
import net.zatrit.openmcskins.io.skins.Cosmetics;
import net.zatrit.openmcskins.io.skins.resolvers.handler.AbstractPlayerHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CosmeticsLoader implements Loader {
    @Override
    public boolean filter(Resolver<?> resolver) {
        return resolver instanceof CosmeticsResolver;
    }

    @Override
    public Object processHandlers(@NotNull List<? extends AbstractPlayerHandler<?>> handlers) {
        List<Cosmetics.CosmeticsItem> allCosmetics = new ArrayList<>();

        handlers.stream().map(x -> ((PlayerCosmeticsHandler) x).downloadCosmetics()).forEach(cosmetics -> {
            if (cosmetics != null) allCosmetics.addAll(cosmetics);
        });

        return allCosmetics;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void doFinally(Object result, @NotNull GameProfile profile, Object[] args) {
        List<Cosmetics.CosmeticsItem> cosmetics = (List<Cosmetics.CosmeticsItem>) result;
        Cosmetics.PLAYER_COSMETICS.put(profile.getName(), cosmetics);
    }
}
