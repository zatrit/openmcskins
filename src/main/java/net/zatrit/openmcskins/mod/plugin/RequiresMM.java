package net.zatrit.openmcskins.mod.plugin;

import net.fabricmc.loader.api.FabricLoader;
import net.zatrit.openmcskins.annotation.KeepClass;

@KeepClass
public class RequiresMM extends ConditionalMixinPlugin {
    public RequiresMM() {
        super(FabricLoader.getInstance().isModLoaded("mm"));
    }
}
