package net.zatrit.openmcskins.resolvers.handler;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.MoreObjects.firstNonNull;

public abstract class AbstractPlayerHandler<TT> {
    protected final Map<MinecraftProfileTexture.Type, TT> textures = new HashMap<>();
    private String model = "default";
    private int index;

    @NotNull
    public final String getModel() {
        return firstNonNull(model, "default");
    }

    protected void setModel(String model) {
        this.model = model;
    }

    public boolean hasTexture(MinecraftProfileTexture.Type type) {
        return textures.containsKey(type);
    }

    public final AbstractPlayerHandler<?> withIndex(int index) {
        this.index = index;
        return this;
    }

    public final int getIndex() {
        return this.index;
    }

    public abstract Identifier downloadTexture(MinecraftProfileTexture.Type type);
}