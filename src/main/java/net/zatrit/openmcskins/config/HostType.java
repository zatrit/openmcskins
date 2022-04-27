package net.zatrit.openmcskins.config;

import net.zatrit.openmcskins.annotation.KeepClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.nodes.Tag;

@KeepClass
public enum HostType {
    MOJANG,
    OPTIFINE,
    LOCAL,
    SERVER,
    ELYBY;

    @Contract(" -> new")
    public @NotNull Tag getTag() {
        return new Tag("!" + this.toString().toLowerCase());
    }
}
