package net.zatrit.openmcskins;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.zatrit.openmcskins.gui.OptionsScreen;

public class OpenMCSkinsModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> new OptionsScreen(screen);
    }
}
