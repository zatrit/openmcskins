package net.zatrit.openmcskins;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.zatrit.openmcskins.annotation.KeepClass;
import net.zatrit.openmcskins.config.OpenMCSkinsConfig;
import net.zatrit.openmcskins.util.ConfigUtils;

import static net.zatrit.openmcskins.OpenMCSkins.translatable;

@KeepClass
public class ModMenuEntry implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (FabricLoader.getInstance().isModLoaded("cloth-config"))
            return parent -> {
                OpenMCSkinsConfig config = OpenMCSkins.getConfig();

                ConfigBuilder configBuilder = ConfigBuilder.create()
                        .setTitle(translatable("openmcskins.config.title"))
                        .setTransparentBackground(true)
                        .setDoesConfirmSave(false);
                ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();

                ConfigCategory general = configBuilder.getOrCreateCategory(translatable("openmcskins.config.category.general"));

                general.addEntry(entryBuilder
                        .startStrList(translatable("openmcskins.config.hosts"), ConfigUtils.hostsToStrings(OpenMCSkins.getConfig()))
                        .setDeleteButtonEnabled(true)
                        .setInsertInFront(true)
                        .setExpanded(true)
                        .setSaveConsumer(config::setHostsString)
                        .build());
                general.addEntry(entryBuilder
                        .startIntField(translatable("openmcskins.config.resolveTimeout"), config.getResolvingTimeout())
                        .setSaveConsumer(config::setResolvingTimeout)
                        .setDefaultValue(5)
                        .setMin(1)
                        .build());
                general.addEntry(entryBuilder
                        .startBooleanToggle(translatable("openmcskins.config.fullErrorMessage"), config.getFullErrorMessage())
                        .setSaveConsumer(config::setFullErrorMessage)
                        .setDefaultValue(false)
                        .build()
                );

                return configBuilder.setFallbackCategory(general).setParentScreen(parent).build();
            };
        else return parent -> null;
    }
}
