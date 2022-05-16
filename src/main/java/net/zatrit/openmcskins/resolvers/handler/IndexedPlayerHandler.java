package net.zatrit.openmcskins.resolvers.handler;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.MoreObjects.firstNonNull;

public abstract class IndexedPlayerHandler<TT> implements Serializable {
    protected final Map<MinecraftProfileTexture.Type, TT> textures = new HashMap<>();
    private String model = "default";
    private int index;

    @Nullable
    public abstract Identifier downloadTexture(MinecraftProfileTexture.Type type);

    @NotNull
    public final String getModelOrDefault() {
        return firstNonNull(model, "default");
    }

    @Contract("_ -> this")
    public final IndexedPlayerHandler<TT> withIndex(int index) {
        this.index = index;
        return this;
    }

    public boolean hasTexture(MinecraftProfileTexture.Type type) {
        return textures.containsKey(type);
    }

    public final int getIndex() {
        return index;
    }

    protected void setModel(String model) {
        this.model = model;
    }
}