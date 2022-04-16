package net.zatrit.openmcskins;

import net.zatrit.openmcskins.annotation.DontObfuscate;
import net.zatrit.openmcskins.enums.SecureMode;
import net.zatrit.openmcskins.resolvers.AbstractResolver;
import net.zatrit.openmcskins.resolvers.MojangAuthlibResolver;

import java.util.ArrayList;
import java.util.List;

public class OpenMCSkinsConfig {
    @DontObfuscate
    public int resolvingTimeout = 5;
    @DontObfuscate
    public List<AbstractResolver<? extends AbstractResolver.IndexedPlayerData>> hosts = new ArrayList<>();

    public OpenMCSkinsConfig() {
        this.hosts.add(new MojangAuthlibResolver(SecureMode.SECURE));
    }
}
