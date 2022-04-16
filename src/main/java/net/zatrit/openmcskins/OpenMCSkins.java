package net.zatrit.openmcskins;

import io.reactivex.rxjava3.internal.functions.Functions;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.zatrit.openmcskins.annotation.KeepClass;
import net.zatrit.openmcskins.resolvers.AbstractResolver;
import net.zatrit.openmcskins.util.ConfigFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@KeepClass
public class OpenMCSkins implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("openmcskins");
    private static OpenMCSkinsConfig config = null;

    public static List<AbstractResolver<? extends AbstractResolver.IndexedPlayerData>> getHosts() {
        return config.hosts;
    }

    public static OpenMCSkinsConfig getConfig() {
        return config;
    }

    @Override
    public void onInitialize() {
        RxJavaPlugins.setErrorHandler(Functions.emptyConsumer());

        Path configFilePath = Paths.get(MinecraftClient.getInstance().runDirectory.getPath(), "config", "openmcskins.yml");
        try {
            config = ConfigFile.load(configFilePath.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
