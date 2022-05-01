package net.zatrit.openmcskins.config;

import com.mojang.authlib.minecraft.UserApiService;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import net.minecraft.client.MinecraftClient;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.annotation.DontObfuscate;
import net.zatrit.openmcskins.mixin.MinecraftClientAccessor;
import net.zatrit.openmcskins.util.ConfigUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@Config(name = OpenMCSkins.MOD_ID)
public class OpenMCSkinsConfig implements ConfigData {
    @DontObfuscate
    public int resolvingTimeout = 5;
    @DontObfuscate
    public @NotNull List<HostConfigItem<?>> hosts = new ArrayList<>();
    @DontObfuscate
    public boolean fullErrorMessage = false;
    @DontObfuscate
    public HashingAlgorithm hashingAlgorithm = HashingAlgorithm.SHA384;

    public OpenMCSkinsConfig() {
        this.hosts.add(new HostConfigItem<>(HostType.OPTIFINE, null));
        boolean offlineMode = ((MinecraftClientAccessor) MinecraftClient.getInstance()).getUserApiService() == UserApiService.OFFLINE;
        this.hosts.add(new HostConfigItem<>(HostType.MOJANG, offlineMode ? AuthlibResolverMode.OFFLINE : AuthlibResolverMode.ONLINE));
    }

    public int getResolvingTimeout() {
        return resolvingTimeout;
    }

    public void setResolvingTimeout(int resolvingTimeout) {
        this.resolvingTimeout = resolvingTimeout;
    }

    public boolean getFullErrorMessage() {
        return this.fullErrorMessage;
    }

    public void setFullErrorMessage(boolean fullErrorMessage) {
        this.fullErrorMessage = fullErrorMessage;
    }

    public void setHashingAlgorithm(HashingAlgorithm algorithm) {
        this.hashingAlgorithm = algorithm;
    }

    public HashingAlgorithm getHashingAlgorithm() {
        return this.hashingAlgorithm;
    }

    public @NotNull List<HostConfigItem<?>> getHosts() {
        return hosts;
    }

    public void setHosts(List<HostConfigItem<?>> hosts) {
        this.hosts = hosts;
    }

    public void setHostsString(List<String> hostStrings) {
        this.setHosts(ConfigUtils.stringsToHost(hostStrings));
    }
}
