package net.zatrit.openmcskins;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.zatrit.openmcskins.config.CosmeticaMode;
import net.zatrit.openmcskins.resolvers.*;

@SuppressWarnings("unused")
public enum HostType {
    CLOAKSPLUS(d -> new DirectResolver("http://161.35.130.99/capes/{name}.png", MinecraftProfileTexture.Type.CAPE)),
    COSMETICA(d -> {
        String url = "https://api.cosmetica.cc/get/cloak?username={name}";
        if (!d.isEmpty()) {
            CosmeticaMode mode = CosmeticaMode.valueOf(d);
            if (mode == CosmeticaMode.NO_THIRD_PARTY) url += "&nothirdparty";
        }
        return new DirectResolver(url, MinecraftProfileTexture.Type.CAPE);
    }),
    DIRECT(d -> {
        String[] values = d.split(":");
        MinecraftProfileTexture.Type type = MinecraftProfileTexture.Type.valueOf(values[0]);
        return new DirectResolver(values[0], type);
    }),
    ELYBY(d -> new SimpleServerResolver("http://skinsystem.ely.by")),
    FIVEZIG(d -> new _5ZigRebornResolver()),
    LABYMOD(d -> new DirectResolver("https://dl.labymod.net/capes/{id}", MinecraftProfileTexture.Type.CAPE)),
    LOCAL(LocalDirectoryResolver::new),
    MINECRAFTCAPES(d -> new MinecraftCapesResolver()),
    MOJANG(d -> new MojangAuthlibResolver()),
    OPTIFINE(d -> new DirectResolver("http://s.optifine.net/capes/{name}.png", MinecraftProfileTexture.Type.CAPE)),
    SERVER(SimpleServerResolver::new),
    TLAUNCHER(d -> new SimpleServerResolver("https://auth.tlauncher.org/skin/profile/texture/login", "%s/%s"));

    private final ResolverConstructor construct;

    HostType(ResolverConstructor construct) {
        this.construct = construct;
    }

    public Resolver<?> createResolver(String data) {
        return construct.construct(data);
    }
}
