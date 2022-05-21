package net.zatrit.openmcskins.mod.plugin;

import net.fabricmc.loader.api.FabricLoader;
import net.zatrit.openmcskins.annotation.KeepClass;
import net.zatrit.openmcskins.mod.OpenMCSkins;

@KeepClass
public class RequiresCEM extends ConditionalMixinPlugin {
    public RequiresCEM() {
        super(FabricLoader.getInstance().isModLoaded("cem"));
    }
}
