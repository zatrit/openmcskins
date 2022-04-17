package net.zatrit.openmcskins;

import io.reactivex.rxjava3.internal.functions.Functions;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.zatrit.openmcskins.annotation.KeepClass;
import net.zatrit.openmcskins.resolvers.AbstractResolver;
import net.zatrit.openmcskins.util.ConfigFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@KeepClass
public class OpenMCSkins implements ModInitializer {
    public static final String MOD_ID = "openmcskins";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static OpenMCSkinsConfig config = null;

    public static List<AbstractResolver<? extends AbstractResolver.IndexedPlayerData>> getHosts() {
        return config.hosts;
    }

    public static OpenMCSkinsConfig getConfig() {
        return config;
    }

    public static void updateConfig() {
        MinecraftClient client = MinecraftClient.getInstance();
        Path configFilePath = Paths.get(client.runDirectory.getPath(), "config", "openmcskins.yml");

        try {
            config = ConfigFile.load(configFilePath.toFile());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (client.world != null) try {
            final String NAMESPACE = "intermediary";
            MappingResolver resolver = FabricLoader.getInstance().getMappingResolver();

            Class<AbstractClientPlayerEntity> entityClass = AbstractClientPlayerEntity.class;
            String entityClassName = resolver.unmapClassName(NAMESPACE, entityClass.getName());
            String getPlayerListEntryName = resolver.mapMethodName(NAMESPACE, entityClassName, "method_3123", "()Lnet/minecraft/class_640;");
            Method getPlayerListEntry = entityClass.getDeclaredMethod(getPlayerListEntryName);
            getPlayerListEntry.setAccessible(true);

            Class<PlayerListEntry> playerClass = PlayerListEntry.class;
            String playerClassName = resolver.unmapClassName(NAMESPACE, playerClass.getName());
            String texturesLoadedName = resolver.mapFieldName(NAMESPACE, playerClassName, "field_3740", "Z");
            Field texturesLoaded = playerClass.getDeclaredField(texturesLoadedName);
            texturesLoaded.setAccessible(true);

            AbstractClientPlayerEntity[] players = client.world.getPlayers().toArray(new AbstractClientPlayerEntity[0]);
            for (int i = 0; i < players.length; i = i + 1) {
                PlayerListEntry entry = (PlayerListEntry) getPlayerListEntry.invoke(players[i]);
                texturesLoaded.set(entry, false);
            }
            texturesLoaded.setAccessible(false);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInitialize() {
        RxJavaPlugins.setErrorHandler(Functions.emptyConsumer());
        updateConfig();
    }
}
