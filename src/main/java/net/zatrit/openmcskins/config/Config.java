package net.zatrit.openmcskins.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.resource.language.I18n;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.annotation.KeepClassMember;
import net.zatrit.openmcskins.config.options.ConfigHostOption;
import net.zatrit.openmcskins.config.options.HashingAlgorithm;
import net.zatrit.openmcskins.config.options.HostType;
import net.zatrit.openmcskins.config.options.UUIDResolutionMode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@me.shedaniel.autoconfig.annotation.Config.Gui.Background(
        value = me.shedaniel.autoconfig.annotation.Config.Gui.Background.TRANSPARENT)
@me.shedaniel.autoconfig.annotation.Config(name = OpenMCSkins.MOD_ID)
public class Config implements ConfigData {
    @ConfigEntry.Category("loader")
    @KeepClassMember
    @ConfigEntry.Gui.Tooltip(count = 2)
    public int resolvingTimeout = 10;
    @ConfigEntry.Category("loader")
    @KeepClassMember
    @ConfigEntry.Gui.Tooltip(count = 2)
    public @NotNull List<ConfigHostOption> hosts = new ArrayList<>();
    @ConfigEntry.Category("loader")
    @KeepClassMember
    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    @ConfigEntry.Gui.Tooltip(count = 4)
    public UUIDResolutionMode uuidResolutionMode = UUIDResolutionMode.AUTO;
    @ConfigEntry.Category("loader")
    @KeepClassMember
    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    @ConfigEntry.Gui.Tooltip(count = 8)
    public HashingAlgorithm hashingAlgorithm = HashingAlgorithm.MURMUR3;
    @ConfigEntry.Category("rendering")
    @KeepClassMember
    @ConfigEntry.Gui.Tooltip()
    public boolean animatedCapes = true;
    @ConfigEntry.Category("rendering")
    @KeepClassMember
    @ConfigEntry.Gui.Tooltip(count = 4)
    public boolean cosmetics = FabricLoader.getInstance().isModLoaded("cem");
    @ConfigEntry.Category("rendering")
    @KeepClassMember
    @ConfigEntry.Gui.Tooltip(count = 4)
    public boolean ears = FabricLoader.getInstance().isModLoaded("mm");
    @ConfigEntry.Category("rendering")
    @KeepClassMember
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean forceIcons = true;
    @ConfigEntry.Category("debug")
    @KeepClassMember
    public boolean fullErrorMessage = false;

    public Config() {
        this.hosts.add(new ConfigHostOption(HostType.OPTIFINE, null));
        this.hosts.add(new ConfigHostOption(HostType.ELYBY, null));
        this.hosts.add(new ConfigHostOption(HostType.MOJANG, null));
    }

    @Override
    public void validatePostLoad() throws ValidationException {
        if (resolvingTimeout < 1) {
            throw new ValidationException(I18n.translate("text.openmcskins.validation_error"));
        }
    }
}
