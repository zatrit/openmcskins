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

@SuppressWarnings("unused")
public enum Hosts {
    CLOAKSPLUS(d -> new DirectResolver("http://161.35.130.99/capes/{name}.png", MinecraftProfileTexture.Type.CAPE)),
    COSMETICA(d -> {
        String url = "https://api.cosmetica.cc/get/cloak?username={name}";
        if (!d.isEmpty()) {
            CosmeticaMode mode = CosmeticaMode.valueOf(d);
            if (mode == CosmeticaMode.NO_THIRD_PARTY)
                url += "&nothirdparty";
        }
        return new DirectResolver(url, MinecraftProfileTexture.Type.CAPE);
    }),
    DIRECT(d -> {
        String[] values = d.split(":");
        MinecraftProfileTexture.Type type = MinecraftProfileTexture.Type.valueOf(values[0]);
        return new DirectResolver(values[0], type);
    }),
    ELYBY(d -> new SimpleServerResolver("http://skinsystem.ely.by", "%s/textures/%s")),
    FIVEZIG(d -> new _5ZigRebornResolver()),
    LABYMOD(d -> new DirectResolver("https://dl.labymod.net/capes/{id}", MinecraftProfileTexture.Type.CAPE)),
    LOCAL(LocalDirectoryResolver::new),
    MINECRAFTCAPES(d -> new MinecraftCapesResolver()),
    MOJANG(d -> new MojangAuthlibResolver()),
    OPTIFINE(d -> new DirectResolver("http://s.optifine.net/capes/{name}.png", MinecraftProfileTexture.Type.CAPE)),
    SERVER(host -> new SimpleServerResolver(host, "%s/textures/%s")),
    TLAUNCHER(d -> new SimpleServerResolver("https://auth.tlauncher.org/skin/profile/texture/login", "%s/%s"));

    private final ResolverConstructor construct;

    Hosts(ResolverConstructor construct) {
        this.construct = construct;
    }

    public Resolver<?> createResolver(String data) {
        return construct.construct(data);
    }
}
