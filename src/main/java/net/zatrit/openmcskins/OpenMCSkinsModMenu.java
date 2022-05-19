package net.zatrit.openmcskins;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import net.zatrit.openmcskins.annotation.KeepClass;
import net.zatrit.openmcskins.config.OpenMCSkinsConfig;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@KeepClass
public class OpenMCSkinsModMenu implements ModMenuApi {
    @Contract(pure = true)
    @Override
    public @NotNull ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(OpenMCSkinsConfig.class, parent).get();
    }
}
