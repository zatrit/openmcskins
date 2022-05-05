package net.zatrit.openmcskins;

import net.zatrit.openmcskins.annotation.KeepClass;
import net.zatrit.openmcskins.config.HostConfigItem;
import net.zatrit.openmcskins.resolvers.*;
import net.zatrit.openmcskins.resolvers.capes.LabyModResolver;
import net.zatrit.openmcskins.resolvers.capes.OptifineCapeResolver;
import net.zatrit.openmcskins.resolvers.capes._5ZigRebornResolver;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.nodes.Tag;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

@KeepClass
public enum HostType {
    ELYBY(d -> new ElyByServerResolver()),
    FIVEZIG(d -> new _5ZigRebornResolver()),
    LABYMOD(d -> new LabyModResolver()),
    LOCAL(LocalDirectoryResolver::new),
    MOJANG(d -> new MojangAuthlibResolver()),
    OPTIFINE(d -> new OptifineCapeResolver()),
    SERVER(SimpleServerResolver::new);

    private final ResolverConstructor construct;

    HostType(ResolverConstructor construct) {
        this.construct = construct;
    }

    @Contract(" -> new")
    public @NotNull Tag getTag() {
        return new Tag("!" + this.toString().toLowerCase());
    }

    public HostConfigItem createHostConfigItem(@Nullable String data) {
        String dataOrEmptyString = firstNonNull(data, "").replace("'", "");
        return switch (this) {
            case LOCAL, SERVER -> new HostConfigItem(this, dataOrEmptyString);
            default -> new HostConfigItem(this, null);
        };
    }

    public AbstractResolver<?> createResolver(String data) {
        return construct.construct(data);
    }
}
