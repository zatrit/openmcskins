package net.zatrit.openmcskins;

import io.reactivex.rxjava3.internal.functions.Functions;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.zatrit.openmcskins.annotation.KeepClass;
import net.zatrit.openmcskins.config.HostConfigItem;
import net.zatrit.openmcskins.config.OpenMCSkinsConfig;
import net.zatrit.openmcskins.mixin.AbstractClientPlayerEntityAccessor;
import net.zatrit.openmcskins.mixin.PlayerListEntryAccessor;
import net.zatrit.openmcskins.resolvers.AbstractResolver;
import net.zatrit.openmcskins.util.ConfigUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

@KeepClass
public class OpenMCSkins implements ModInitializer {
    public static final String MOD_ID = "openmcskins";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final String mappingNamespace = "intermediary";
    private static final MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
    private static final File configFile = Paths.get(MinecraftClient.getInstance().runDirectory.getPath(), "config", "openmcskins.yml").toFile();
    private static OpenMCSkinsConfig config = null;
    private static List<? extends AbstractResolver<?>> resolvers;

    public static OpenMCSkinsConfig getConfig() {
        if (config == null) reloadConfig();
        return config;
    }

    public static void reloadConfig() {
        try {
            config = ConfigUtil.load(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        invalidateAllResolvers();
    }

    public static File getConfigFile() {
        return configFile;
    }

    public static List<? extends AbstractResolver<?>> getResolvers() {
        if (resolvers == null)
            resolvers = getConfig().getHosts().stream().map(HostConfigItem::createResolver).toList();
        return resolvers;
    }

    public static void invalidateAllResolvers() {
        OpenMCSkins.resolvers = null;

        MinecraftClient client = MinecraftClient.getInstance();

        if (client.world != null) try {
            AbstractClientPlayerEntity[] players = client.world.getPlayers().toArray(new AbstractClientPlayerEntity[0]);
            for (AbstractClientPlayerEntity player : players) {
                PlayerListEntry entry = ((AbstractClientPlayerEntityAccessor) player).invokeGetPlayerListEntry();
                ((PlayerListEntryAccessor) entry).setTexturesLoaded(false);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull Text translatable(String key) {
        return new TranslatableText(key);
    }

    @Override
    public void onInitialize() {
        RxJavaPlugins.setErrorHandler(Functions.emptyConsumer());
    }
}
