package net.zatrit.openmcskins.config.options;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.nodes.Tag;
import net.zatrit.openmcskins.api.resolver.Resolver;
import net.zatrit.openmcskins.api.resolver.ResolverConstructor;
import net.zatrit.openmcskins.io.skins.resolvers.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public enum HostType {
    CLOAKSPLUS(d -> new OptifineResolver("https://server.cloaksplus.com")),
    COSMETICA(d -> {
        String url = "https://api.cosmetica.cc/get/cloak?username={name}";
        if (!d.isEmpty()) {
            final CosmeticaMode mode = CosmeticaMode.valueOf(d);
            if (mode == CosmeticaMode.NO_THIRD_PARTY) url += "&nothirdparty";
        }
        return new DirectResolver(url, MinecraftProfileTexture.Type.CAPE);
    }),
    DIRECT(d -> {
        final String[] values = d.split(":");
        final MinecraftProfileTexture.Type type = MinecraftProfileTexture.Type.valueOf(values[0]);
        return new DirectResolver(values[0], type);
    }),
    ELYBY(d -> new SimpleServerResolver("http://skinsystem.ely.by")),
    FIVEZIG(d -> new _5ZigRebornResolver()),
    LABYMOD(d -> new DirectResolver("https://dl.labymod.net/capes/{id}", MinecraftProfileTexture.Type.CAPE)),
    LOCAL(LocalDirectoryResolver::new),
    MANTLE(d -> new OptifineResolver("http://35.190.10.249")),
    MINECRAFTCAPES(d -> new MinecraftCapesResolver()),
    MOJANG(d -> new MojangAuthlibResolver()),
    OPTIFINE(d -> new OptifineResolver("http://s.optifine.net")),
    SERVER(SimpleServerResolver::new),
    TLAUNCHER(d -> new SimpleServerResolver("https://auth.tlauncher.org/skin/profile/texture/login", "%s/%s"));

    private final ResolverConstructor construct;

    HostType(ResolverConstructor construct) {
        this.construct = construct;
    }

    @Contract(" -> new")
    public me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.nodes.@NotNull Tag getTag() {
        return new Tag("!" + this.toString().toLowerCase());
    }

    public Resolver<?> createResolver(String data) {
        return construct.construct(data);
    }
}
