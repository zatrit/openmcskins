package net.zatrit.openmcskins.resolvers.handler;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.zatrit.openmcskins.interfaces.handler.PlayerVanillaHandler;
import net.zatrit.openmcskins.util.IndexedObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.MoreObjects.firstNonNull;

public abstract class AbstractPlayerHandler<TT> extends IndexedObject implements PlayerVanillaHandler {
    protected final Map<MinecraftProfileTexture.Type, TT> textures = new HashMap<>();
    private String model = "default";

    @NotNull
    public final String getModel() {
        return firstNonNull(model, "default");
    }

    public boolean hasTexture(MinecraftProfileTexture.Type type) {
        return textures.containsKey(type);
    }

    protected void setModel(String model) {
        this.model = model;
    }
}