package net.zatrit.openmcskins;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.zatrit.openmcskins.config.CosmeticaMode;
import net.zatrit.openmcskins.config.HostConfigItem;
import net.zatrit.openmcskins.resolvers.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.nodes.Tag;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public enum Hosts {
    DIRECT(d -> {
        String[] values = d.split(":");
        MinecraftProfileTexture.Type type = MinecraftProfileTexture.Type.valueOf(values[0]);
        return new DirectResolver(values[0], type);
    }),
    COSMETICA(d -> {
        String url = "https://api.cosmetica.cc/get/cloak?username={name}";
        if (!d.isEmpty()) {
            CosmeticaMode mode = CosmeticaMode.valueOf(d);
            if (mode == CosmeticaMode.NO_THIRD_PARTY)
                url += "&nothirdparty";
        }
        return new DirectResolver(url, MinecraftProfileTexture.Type.CAPE);
    }),
    ELYBY(d -> new SimpleServerResolver("http://skinsystem.ely.by")),
    FIVEZIG(d -> new _5ZigRebornResolver()),
    LABYMOD(d -> new DirectResolver("https://dl.labymod.net/capes/{id}", MinecraftProfileTexture.Type.CAPE)),
    LOCAL(LocalDirectoryResolver::new),
    MOJANG(d -> new MojangAuthlibResolver()),
    MINECRAFTCAPES(d -> new MinecraftCapesResolver()),
    OPTIFINE(d -> new DirectResolver("http://s.optifine.net/capes/{name}.png", MinecraftProfileTexture.Type.CAPE)),
    SERVER(SimpleServerResolver::new);

    private final ResolverConstructor construct;

    Hosts(ResolverConstructor construct) {
        this.construct = construct;
    }

    @Contract(" -> new")
    public @NotNull Tag getTag() {
        return new Tag("!" + this.toString().toLowerCase());
    }

    public HostConfigItem createHostConfigItem(@Nullable String data) {
        String dataOrEmptyString = firstNonNull(data, "").replace("'", "");
        return switch (this) {
            case LOCAL, SERVER, COSMETICA -> new HostConfigItem(this, dataOrEmptyString);
            default -> new HostConfigItem(this, null);
        };
    }

    public Resolver<?> createResolver(String data) {
        return construct.construct(data);
    }
}
