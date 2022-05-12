package net.zatrit.openmcskins;

import com.google.common.hash.HashFunction;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.registry.GuiRegistry;
import me.shedaniel.autoconfig.serializer.YamlConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.DumperOptions;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.Yaml;
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
import net.zatrit.openmcskins.config.OpenMCSkinsConfig;
import net.zatrit.openmcskins.mixin.AbstractClientPlayerEntityAccessor;
import net.zatrit.openmcskins.mixin.PlayerListEntryAccessor;
import net.zatrit.openmcskins.resolvers.Resolver;
import net.zatrit.openmcskins.util.ConfigUtil;
import net.zatrit.openmcskins.util.yaml.ConfigConstructor;
import net.zatrit.openmcskins.util.yaml.ConfigRepresenter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

@KeepClass
@Environment(EnvType.CLIENT)
public class OpenMCSkins implements ClientModInitializer {
    public static final String MOD_ID = "openmcskins";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static List<? extends Resolver<?>> resolvers;

    public static OpenMCSkinsConfig getConfig() {
        return AutoConfig.getConfigHolder(OpenMCSkinsConfig.class).getConfig();
    }

    public static List<? extends Resolver<?>> getResolvers() {
        if (resolvers == null) resolvers = getConfig().hosts.stream().parallel().map(x -> {
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
        if (OpenMCSkins.getConfig().fullErrorMessage) error.printStackTrace();
        else OpenMCSkins.LOGGER.error(error.getMessage());
    }

    public static HashFunction getHashFunction() {
        return getConfig().hashingAlgorithm.getFunction();
    }

    public static void reloadConfig() {
        AutoConfig.getConfigHolder(OpenMCSkinsConfig.class).load();
        invalidateAllResolvers();
    }

    public static void invalidateAllResolvers() {
        OpenMCSkins.resolvers = null;
        TextureLoader.getUuidCache().cleanUp();
        TextureLoader.clearTextures();

        MinecraftClient client = MinecraftClient.getInstance();

        if (client.world != null) {
            AbstractClientPlayerEntity[] players = client.world.getPlayers().toArray(new AbstractClientPlayerEntity[0]);
            for (AbstractClientPlayerEntity player : players) {
                PlayerListEntry entry = ((AbstractClientPlayerEntityAccessor) player).invokeGetPlayerListEntry();
                if (entry != null) ((PlayerListEntryAccessor) entry).setTexturesLoaded(false);
            }
        }
    }

    @Override
    public void onInitializeClient() {
        RxJavaPlugins.setErrorHandler(OpenMCSkins::handleError);
        AutoConfig.register(OpenMCSkinsConfig.class, (d, c) -> {
            DumperOptions dumperOptions = new DumperOptions();
            dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            Yaml yaml = new Yaml(new ConfigConstructor(), new ConfigRepresenter(), dumperOptions);
            return new YamlConfigSerializer<>(d, c, yaml);
        });

        GuiRegistry registry = AutoConfig.getGuiRegistry(OpenMCSkinsConfig.class);

        ConfigEntryBuilder builder = ConfigEntryBuilder.create();

        registry.registerTypeProvider((s, field, o, o1, guiRegistryAccess) -> {
            List<String> hosts = ConfigUtil.getHostsAsStrings((OpenMCSkinsConfig) o);
            Text text = new TranslatableText("text.autoconfig.openmcskins.option.hosts");

            StringListBuilder hostList = builder.startStrList(text, hosts).setInsertInFront(true).setSaveConsumer(x -> {
                try {
                    field.set(o, ConfigUtil.getHostsFromStrings(x));
                } catch (IllegalAccessException e) {
                    OpenMCSkins.handleError(e);
                }
            });
            return List.of(hostList.build());
        }, List.class);
    }
}
