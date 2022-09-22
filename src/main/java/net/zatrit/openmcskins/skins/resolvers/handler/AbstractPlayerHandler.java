package net.zatrit.openmcskins.skins.resolvers.handler;

import com.google.common.base.MoreObjects;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractPlayerHandler<TT> {
    protected final Map<MinecraftProfileTexture.Type, TT> textures = new HashMap<>();
    private String model = "default";
    private int index;

    @NotNull
    public final String getModel() {
        return MoreObjects.firstNonNull(model, "default");
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