package net.zatrit.openmcskins.operators;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.nodes.Tag;
import net.zatrit.openmcskins.api.resolver.Resolver;
import net.zatrit.openmcskins.api.resolver.ResolverConstructor;
import net.zatrit.openmcskins.skins.resolvers.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public enum Host {
    CLOAKSPLUS(d -> new OptifineResolver("https://server.cloaksplus.com")),
    COSMETICA(d -> {
        String url = "https://api.cosmetica.cc/get/cloak?username={name}";
        if (!d.isEmpty()) {
            if (CosmeticaMode.valueOf(d.toUpperCase()) ==
                    CosmeticaMode.NO_THIRD_PARTY) {
                url += "&nothirdparty";
            }
        }
        return new DirectResolver(url, MinecraftProfileTexture.Type.CAPE);
    }),
    DIRECT(d -> {
        final String[] values = d.split(":");
        final MinecraftProfileTexture.Type type = MinecraftProfileTexture.Type.valueOf(
                values[0]);
        return new DirectResolver(values[0], type);
    }),
    ELYBY(d -> new SimpleServerResolver("http://skinsystem.ely.by")),
    FIVEZIG(d -> new _5ZigRebornResolver()),
    LABYMOD(d -> new DirectResolver("https://dl.labymod.net/capes/{id}",
            MinecraftProfileTexture.Type.CAPE)),
    LOCAL(LocalDirectoryResolver::new),
    MANTLE(d -> new OptifineResolver("http://35.190.10.249")),
    MINECRAFTCAPES(d -> new MinecraftCapesResolver()),

    MOJANG(d -> {
        RefillProfiles refillProfiles = RefillProfiles.REFILL_EMPTY;
        if (!d.isEmpty()) {
            refillProfiles = RefillProfiles.valueOf(d.toUpperCase());
        }
        return new MojangAuthlibResolver(refillProfiles);
    }),

    VALHALLA(d -> {
        var refillProfiles = RefillProfiles.REFILL_EMPTY;
        final var split = d.split(";");
        var url = split[0];
        if (split.length > 2 && !split[1].isEmpty()) {
            refillProfiles = RefillProfiles.valueOf(d.toUpperCase());
        }
        return new SimpleServerResolver(url, "%s/api/v1/user/%s", refillProfiles);
    }),
    OPTIFINE(d -> new OptifineResolver("http://s.optifine.net")),
    SERVER(SimpleServerResolver::new),
    TLAUNCHER(d -> new SimpleServerResolver(
            "https://auth.tlauncher.org/skin/profile/texture/login",
            "%s/%s")),
    WYNNTILS(d -> new DirectResolver(
            "https://athena.wynntils.com/capes/user/{id}",
            MinecraftProfileTexture.Type.CAPE));

    private final ResolverConstructor construct;

    Host(ResolverConstructor construct) {
        this.construct = construct;
    }

    @Contract(" -> new")
    public @NotNull Tag getTag() {
        return new Tag("!" + this.toString().toLowerCase());
    }

    public Resolver<?> createResolver(String data) {
        return construct.construct(data);
    }
}
