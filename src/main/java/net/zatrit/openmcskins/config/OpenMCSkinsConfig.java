package net.zatrit.openmcskins.config;

import com.mojang.authlib.minecraft.UserApiService;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.zatrit.openmcskins.HostType;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.annotation.KeepClassMember;
import net.zatrit.openmcskins.mixin.MinecraftClientAccessor;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Config.Gui.Background(value = Config.Gui.Background.TRANSPARENT)
@Config(name = OpenMCSkins.MOD_ID)
public class OpenMCSkinsConfig implements ConfigData, Serializable {
    @KeepClassMember
    public int resolvingTimeout = 5;
    @KeepClassMember
    public @NotNull List<HostConfigItem> hosts = new ArrayList<>();
    @KeepClassMember
    public boolean fullErrorMessage = false;
    @KeepClassMember
    public boolean offlineMode;
    @KeepClassMember
    public boolean ignoreAnimatedCapes = false;
    @KeepClassMember
    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    @ConfigEntry.Gui.Tooltip(count = 7)
    public HashingAlgorithm hashingAlgorithm = HashingAlgorithm.SHA384;
    @KeepClassMember
    @ConfigEntry.Gui.Tooltip(count = 4)
    public boolean cosmetics;

    public OpenMCSkinsConfig() {
        this.offlineMode = ((MinecraftClientAccessor) MinecraftClient.getInstance()).getUserApiService() == UserApiService.OFFLINE;
        this.cosmetics = FabricLoader.getInstance().isModLoaded("cem");

        this.hosts.add(new HostConfigItem(HostType.OPTIFINE, null));
        this.hosts.add(new HostConfigItem(HostType.ELYBY, null));
        this.hosts.add(new HostConfigItem(HostType.MOJANG, null));
    }

    @Override
    public void validatePostLoad() throws ValidationException {
        if (resolvingTimeout < 1) throw new ValidationException(I18n.translate("text.openmcskins.validation_error"));
    }
}
