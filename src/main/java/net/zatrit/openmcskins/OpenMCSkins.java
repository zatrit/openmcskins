package net.zatrit.openmcskins;

import com.google.common.hash.HashFunction;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.util.version.SemanticVersionImpl;
import net.fabricmc.loader.util.version.SemanticVersionPredicateParser;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.zatrit.openmcskins.api.resolver.Resolver;
import net.zatrit.openmcskins.config.Config;
import net.zatrit.openmcskins.io.skins.PlayerRegistry;
import net.zatrit.openmcskins.mod.mixin.AbstractClientPlayerEntityAccessor;
import net.zatrit.openmcskins.mod.mixin.PlayerListEntryAccessor;
import net.zatrit.openmcskins.render.CosmeticsFeatureRenderer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class OpenMCSkins {
    public static final String MOD_ID = "openmcskins";
    public static final Logger LOGGER = LoggerFactory.getLogger("OpenMCSkins");
    private static Resolver<?>[] resolvers;

    public static Config getConfig() {
        return AutoConfig.getConfigHolder(Config.class).getConfig();
    }

    public static synchronized Resolver<?>[] getResolvers() {
        if (resolvers == null) resolvers = (Resolver<?>[]) getConfig().hosts.stream().parallel().map(x -> {
            try {
                return x.createResolver();
            } catch (Exception ex) {
                OpenMCSkins.handleError(Optional.of(ex));
                return null;
            }
        }).filter(Objects::nonNull).toArray(Resolver[]::new);
        return resolvers;
    }


    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "ResultOfMethodCallIgnored"})
    public static void handleError(Optional<Throwable> error) {
        if (OpenMCSkins.getConfig().fullErrorMessage) error.ifPresent(Throwable::printStackTrace);
        else error.ifPresent(Throwable::getMessage);
    }

    public static HashFunction getHashFunction() {
        return getConfig().hashingAlgorithm.getFunction();
    }

    public static synchronized void invalidateAllResolvers() {
        OpenMCSkins.resolvers = null;
        PlayerRegistry.clear();
        CosmeticsFeatureRenderer.clear();
        Arrays.stream(getResolvers()).forEach(Resolver::clear);

        MinecraftClient client = MinecraftClient.getInstance();

        if (client.world != null) {
            AbstractClientPlayerEntity[] players = client.world.getPlayers().toArray(new AbstractClientPlayerEntity[0]);
            for (AbstractClientPlayerEntity player : players) {
                final PlayerListEntry entry = ((AbstractClientPlayerEntityAccessor) player).invokeGetPlayerListEntry();
                if (entry != null) ((PlayerListEntryAccessor) entry).setTexturesLoaded(false);
            }
        }
    }

    @SuppressWarnings({"deprecation", "OptionalGetWithoutIsPresent"})
    public static boolean isModLoaded(String name, String version) {
        try {
            final var predicate = SemanticVersionPredicateParser.create(version);
            final var modVersion = new SemanticVersionImpl(FabricLoader.getInstance().getModContainer(name).get().getMetadata().getVersion().getFriendlyString(), false);
            return predicate.test(modVersion);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isModLoaded(@NotNull String name) {
        final String[] splitted = name.split(":");
        if (splitted.length > 1)
            return isModLoaded(splitted[0], splitted[1]);
        else
            return isModLoaded(splitted[0], "*");
    }
}
