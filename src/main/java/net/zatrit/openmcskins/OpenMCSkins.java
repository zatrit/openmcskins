package net.zatrit.openmcskins;

import io.reactivex.rxjava3.internal.functions.Functions;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import net.zatrit.openmcskins.annotation.KeepClass;
import net.zatrit.openmcskins.config.ConfigFile;
import net.zatrit.openmcskins.resolvers.AbstractResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@KeepClass
public class OpenMCSkins implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("openmcskins");
    public static final List<AbstractResolver<? extends AbstractResolver.PlayerData>> HOSTS = new ArrayList<>();
    public static ConfigFile CONFIG_FILE = null;

    @Override
    public void onInitialize() {
        RxJavaPlugins.setErrorHandler(Functions.emptyConsumer());

        Path configFilePath = Paths.get(Minecraft.getInstance().gameDirectory.getPath(), "config", "openmcskins.yml");
        try {
            CONFIG_FILE = ConfigFile.load(configFilePath.toFile());
            HOSTS.addAll(CONFIG_FILE.hosts);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
