package net.zatrit.openmcskins.skins.loader;

import com.mojang.authlib.GameProfile;
import net.zatrit.openmcskins.api.handler.PlayerCosmeticsHandler;
import net.zatrit.openmcskins.api.resolver.CosmeticsResolver;
import net.zatrit.openmcskins.api.resolver.Resolver;
import net.zatrit.openmcskins.skins.CosmeticsParser;
import net.zatrit.openmcskins.skins.resolvers.handler.AbstractPlayerHandler;
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
        final List<CosmeticsParser.CosmeticsItem> allCosmetics = new ArrayList<>();

        handlers.stream().map(x -> ((PlayerCosmeticsHandler) x).downloadCosmetics()).forEach(cosmetics -> {
            if (cosmetics != null) {
                allCosmetics.addAll(cosmetics);
            }
        });

        return allCosmetics;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void doFinally(Object result, @NotNull GameProfile profile, Object @NotNull [] args) {
        final List<CosmeticsParser.CosmeticsItem> cosmetics = (List<CosmeticsParser.CosmeticsItem>) result;
        ((CosmeticsResolveCallback) args[0]).onCosmeticsResolved(cosmetics);
    }

    @FunctionalInterface
    public interface CosmeticsResolveCallback {
        void onCosmeticsResolved(List<CosmeticsParser.CosmeticsItem> cosmetics);
    }
}
