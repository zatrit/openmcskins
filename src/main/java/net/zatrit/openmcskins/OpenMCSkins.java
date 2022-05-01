package net.zatrit.openmcskins;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import io.reactivex.rxjava3.internal.functions.Functions;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import net.fabricmc.api.ModInitializer;
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
import net.zatrit.openmcskins.util.ConfigUtils;
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
    public static final HashFunction SKIN_HASH_FUNCTION = Hashing.crc32();
    private static final File configFile = Paths.get(MinecraftClient.getInstance().runDirectory.getPath(), "config", "openmcskins.yml").toFile();
    private static OpenMCSkinsConfig config = null;
    private static List<? extends AbstractResolver<?>> resolvers;

    public static OpenMCSkinsConfig getConfig() {
        if (config == null) reloadConfig();
        return config;
    }

    public static void reloadConfig() {
        try {
            config = ConfigUtils.load(configFile);
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


    public static void handleError(@NotNull Throwable error) {
        if (OpenMCSkins.getConfig().getFullErrorMessage())
            error.printStackTrace();
        else
            OpenMCSkins.LOGGER.error(error.getMessage());
    }

    public static void invalidateAllResolvers() {
        OpenMCSkins.resolvers = null;

        MinecraftClient client = MinecraftClient.getInstance();

        if (client.world != null) {
            AbstractClientPlayerEntity[] players = client.world.getPlayers().toArray(new AbstractClientPlayerEntity[0]);
            for (AbstractClientPlayerEntity player : players) {
                PlayerListEntry entry = ((AbstractClientPlayerEntityAccessor) player).invokeGetPlayerListEntry();
                ((PlayerListEntryAccessor) entry).setTexturesLoaded(false);
            }
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
