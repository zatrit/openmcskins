package net.zatrit.openmcskins;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.hash.HashFunction;
import io.reactivex.rxjava3.internal.functions.Functions;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.StringListBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.zatrit.openmcskins.annotation.KeepClass;
import net.zatrit.openmcskins.resolvers.AbstractResolver;
import net.zatrit.openmcskins.config.OpenMCSkinsConfig;
import net.zatrit.openmcskins.config.SnakeYamlSerializer;
import net.zatrit.openmcskins.mixin.AbstractClientPlayerEntityAccessor;
import net.zatrit.openmcskins.mixin.PlayerListEntryAccessor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@KeepClass
@Environment(EnvType.CLIENT)
public class OpenMCSkins implements ClientModInitializer {
    public static final String MOD_ID = "openmcskins";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static List<? extends AbstractResolver<?>> resolvers;
    private static final Cache<String, UUID> uuidCache = CacheBuilder.newBuilder().build();

    public static OpenMCSkinsConfig getConfig() {
        return AutoConfig.getConfigHolder(OpenMCSkinsConfig.class).getConfig();
    }

    public static List<? extends AbstractResolver<?>> getResolvers() {
        if (resolvers == null) resolvers = getConfig().getHosts().stream().map(x -> {
            try {
                return x.createResolver();
            } catch (Exception ex) {
                OpenMCSkins.handleError(ex);
                return null;
            }
        }).filter(Objects::nonNull).toList();
        return resolvers;
    }


    public static void handleError(@NotNull Throwable error) {
        if (OpenMCSkins.getConfig().getFullErrorMessage()) error.printStackTrace();
        else OpenMCSkins.LOGGER.error(error.getMessage());
    }

    public static HashFunction getHashFunction() {
        return getConfig().getHashingAlgorithm().getFunction();
    }

    public static void reloadConfig() {
        AutoConfig.getConfigHolder(OpenMCSkinsConfig.class).load();
        invalidateAllResolvers();
    }

    public static void invalidateAllResolvers() {
        OpenMCSkins.resolvers = null;
        OpenMCSkins.uuidCache.cleanUp();

        MinecraftClient client = MinecraftClient.getInstance();

        if (client.world != null) {
            AbstractClientPlayerEntity[] players = client.world.getPlayers().toArray(new AbstractClientPlayerEntity[0]);
            for (AbstractClientPlayerEntity player : players) {
                PlayerListEntry entry = ((AbstractClientPlayerEntityAccessor) player).invokeGetPlayerListEntry();
                ((PlayerListEntryAccessor) entry).setTexturesLoaded(false);
            }
        }
    }

    @Contract(value = "_, _ -> new", pure = true)
    private static @NotNull Text translatable(String key, String... s) {
        return new TranslatableText(key, s);
    }

    @Contract(pure = true)
    public static @NotNull Cache<String, UUID> getUuidCache() {
        return uuidCache;
    }

    @Override
    public void onInitializeClient() {
        RxJavaPlugins.setErrorHandler(Functions.emptyConsumer());
        AutoConfig.register(OpenMCSkinsConfig.class, SnakeYamlSerializer::new);

        GuiRegistry registry = AutoConfig.getGuiRegistry(OpenMCSkinsConfig.class);

        ConfigEntryBuilder builder = ConfigEntryBuilder.create();

        registry.registerTypeProvider((s, field, o, o1, guiRegistryAccess) -> {
            List<String> hosts = SnakeYamlSerializer.getHostsAsStrings((OpenMCSkinsConfig) o);
            Text text = translatable("text.autoconfig.openmcskins.option.hosts", String.valueOf(hosts.size()));

            StringListBuilder hostList = builder.startStrList(text, hosts).setInsertInFront(true).setSaveConsumer(x -> {
                try {
                    field.set(o, SnakeYamlSerializer.getHostsFromStrings(x));
                } catch (IllegalAccessException e) {
                    OpenMCSkins.handleError(e);
                }
            });
            return List.of(hostList.build());
        }, List.class);
    }
}
