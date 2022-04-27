package net.zatrit.openmcskins.config;

import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.annotation.DontObfuscate;
import net.zatrit.openmcskins.util.ConfigUtil;
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

    public OpenMCSkinsConfig() {
        this.file = OpenMCSkins.getConfigFile();

        this.hosts.add(new HostConfigItem<>(HostType.OPTIFINE, null));
        this.hosts.add(new HostConfigItem<>(HostType.MOJANG, SecureMode.SECURE));
    }

    public int getResolvingTimeout() {
        return resolvingTimeout;
    }

    public void setResolvingTimeout(int resolvingTimeout) {
        this.resolvingTimeout = resolvingTimeout;
        this.save();
    }

    public @NotNull List<HostConfigItem<?>> getHosts() {
        return hosts;
    }

    public void setHosts(List<HostConfigItem<?>> hosts) {
        this.hosts = hosts;
        this.save();
    }

    private void save() {
        try {
            ConfigUtil.save(this, this.file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setHostsString(List<String> hostStrings) {
        this.setHosts(ConfigUtil.stringsToHost(hostStrings));
    }
}
