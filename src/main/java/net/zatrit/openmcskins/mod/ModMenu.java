package net.zatrit.openmcskins.mod;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import net.zatrit.openmcskins.annotation.KeepClass;
import net.zatrit.openmcskins.mod.config.OpenMCSkinsConfig;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@KeepClass
public class ModMenu implements ModMenuApi {
    @Contract(pure = true)
    @Override
    public @NotNull ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(OpenMCSkinsConfig.class, parent).get();
    }
}
