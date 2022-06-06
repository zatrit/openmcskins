package net.zatrit.openmcskins.mod;

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
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.annotation.KeepClass;
import net.zatrit.openmcskins.config.Config;
import net.zatrit.openmcskins.config.ConfigUtil;
import net.zatrit.openmcskins.config.yaml.ConfigConstructor;
import net.zatrit.openmcskins.config.yaml.ConfigRepresenter;

import java.util.List;

@KeepClass
@Environment(EnvType.CLIENT)
public class ModInit implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AutoConfig.register(Config.class, (d, c) -> {
            final DumperOptions dumperOptions = new DumperOptions();
            dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            final Yaml yaml = new Yaml(new ConfigConstructor(), new ConfigRepresenter(), dumperOptions);
            return new YamlConfigSerializer<>(d, c, yaml);
        });

        final GuiRegistry registry = AutoConfig.getGuiRegistry(Config.class);
        final ConfigEntryBuilder builder = ConfigEntryBuilder.create();

        registry.registerTypeProvider((s, field, o, o1, guiRegistryAccess) -> {
            final List<String> hosts = ConfigUtil.getHostsAsStrings((Config) o);
            final Text text = Text.of(I18n.translate("text.autoconfig.openmcskins.option.hosts"));

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
