package net.zatrit.openmcskins.config;

import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.annotation.DontObfuscate;
import net.zatrit.openmcskins.util.ConfigUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OpenMCSkinsConfig {
    private final File file;
    @DontObfuscate
    public int resolvingTimeout = 5;
    @DontObfuscate
    public @NotNull List<HostConfigItem<?>> hosts = new ArrayList<>();
    @DontObfuscate
    public boolean fullErrorMessage;

    public OpenMCSkinsConfig() {
        this.file = OpenMCSkins.getConfigFile();

        this.hosts.add(new HostConfigItem<>(HostType.OPTIFINE, null));
        this.hosts.add(new HostConfigItem<>(HostType.MOJANG, AuthlibResolverMode.ONLINE));
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

    public @NotNull List<HostConfigItem<?>> getHosts() {
        return hosts;
    }

    public void setHosts(List<HostConfigItem<?>> hosts) {
        this.hosts = hosts;
        this.save();
    }

    public void setHostsString(List<String> hostStrings) {
        this.setHosts(ConfigUtils.stringsToHost(hostStrings));
    }

    private void save() {
        OpenMCSkins.LOGGER.info("Saving config...");
        try {
            ConfigUtils.save(this, this.file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
