package net.zatrit.openmcskins.config;

import com.mojang.authlib.minecraft.UserApiService;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.zatrit.openmcskins.HostType;
import net.zatrit.openmcskins.mod.OpenMCSkins;
import net.zatrit.openmcskins.annotation.KeepClassMember;
import net.zatrit.openmcskins.mod.mixin.MinecraftClientAccessor;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Config.Gui.Background(value = Config.Gui.Background.TRANSPARENT)
@Config(name = OpenMCSkins.MOD_ID)
public class OpenMCSkinsConfig implements ConfigData, Serializable {
    @ConfigEntry.Category("loader")
    @KeepClassMember
    public int resolvingTimeout = 5;
    @ConfigEntry.Category("loader")
    @KeepClassMember
    public @NotNull List<HostConfigItem> hosts = new ArrayList<>();
    @ConfigEntry.Category("loader")
    @KeepClassMember
    public boolean offlineMode = ((MinecraftClientAccessor) MinecraftClient.getInstance()).getUserApiService() == UserApiService.OFFLINE;
    @ConfigEntry.Category("loader")
    @KeepClassMember
    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    @ConfigEntry.Gui.Tooltip(count = 7)
    public HashingAlgorithm hashingAlgorithm = HashingAlgorithm.SHA384;
    @ConfigEntry.Category("rendering")
    @KeepClassMember
    public boolean ignoreAnimatedCapes = false;
    @ConfigEntry.Category("rendering")
    @KeepClassMember
    @ConfigEntry.Gui.Tooltip(count = 4)
    public boolean cosmetics = OpenMCSkins.HAS_CEM_MOD;
    @ConfigEntry.Category("rendering")
    @KeepClassMember
    @ConfigEntry.Gui.Tooltip(count = 4)
    public boolean ears = OpenMCSkins.HAS_MM_MOD;
    @ConfigEntry.Category("rendering")
    @KeepClassMember
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean forceIcons = true;
    @ConfigEntry.Category("debug")
    @KeepClassMember
    public boolean fullErrorMessage = false;

    public OpenMCSkinsConfig() {
        this.hosts.add(new HostConfigItem(HostType.OPTIFINE, null));
        this.hosts.add(new HostConfigItem(HostType.ELYBY, null));
        this.hosts.add(new HostConfigItem(HostType.MOJANG, null));
    }

    @Override
    public void validatePostLoad() throws ValidationException {
        if (resolvingTimeout < 1) throw new ValidationException(I18n.translate("text.openmcskins.validation_error"));
    }
}
