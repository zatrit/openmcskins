package net.zatrit.openmcskins;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import net.zatrit.openmcskins.annotation.KeepClass;
import net.zatrit.openmcskins.config.OpenMCSkinsConfig;

@KeepClass
public class ModMenuEntry implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(OpenMCSkinsConfig.class, parent).get();
    }
}
