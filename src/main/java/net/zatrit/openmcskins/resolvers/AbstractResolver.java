package net.zatrit.openmcskins.resolvers;

import com.google.gson.Gson;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.Serializable;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public abstract class AbstractResolver<D extends AbstractResolver.PlayerData> {
    protected static final Gson GSON = new Gson();

    public abstract D resolvePlayer(PlayerInfo playerInfo) throws IOException;

    public abstract String getName();

    public abstract static class PlayerData implements Serializable {
        public int index;
        public String model = "default";

        public abstract ResourceLocation downloadTexture(MinecraftProfileTexture.Type type);

        public abstract boolean hasTexture(MinecraftProfileTexture.Type type);

        public String getModelOrDefault() {
            return firstNonNull(model, "default");
        }
    }
}
