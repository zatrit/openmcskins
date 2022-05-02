package net.zatrit.openmcskins.config;

import com.mojang.authlib.minecraft.UserApiService;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.annotation.KeepClassMember;
import net.zatrit.openmcskins.mixin.MinecraftClientAccessor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Config(name = OpenMCSkins.MOD_ID)
public class OpenMCSkinsConfig implements ConfigData {
    @KeepClassMember
    public int resolvingTimeout = 5;
    @KeepClassMember
    public @NotNull List<HostConfigItem> hosts = new ArrayList<>();
    @KeepClassMember
    public boolean fullErrorMessage = false;
    @KeepClassMember
    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    @ConfigEntry.Gui.Tooltip(count = 7)
    public HashingAlgorithm hashingAlgorithm = HashingAlgorithm.SHA384;

    public OpenMCSkinsConfig() {
        this.hosts.add(new HostConfigItem(HostType.OPTIFINE, null));
        boolean offlineMode = ((MinecraftClientAccessor) MinecraftClient.getInstance()).getUserApiService() == UserApiService.OFFLINE;
        this.hosts.add(new HostConfigItem(HostType.MOJANG, String.valueOf(offlineMode ? AuthlibResolverMode.OFFLINE : AuthlibResolverMode.ONLINE)));
    }

    public int getResolvingTimeout() {
        return resolvingTimeout;
    }

    public boolean getFullErrorMessage() {
        return this.fullErrorMessage;
    }

    public HashingAlgorithm getHashingAlgorithm() {
        return this.hashingAlgorithm;
    }

    public @NotNull List<HostConfigItem> getHosts() {
        return hosts;
    }

    @Override
    public void validatePostLoad() throws ValidationException {
        if (resolvingTimeout < 1)
            throw new ValidationException(I18n.translate("text.openmcskins.validation_error"));
    }
}
