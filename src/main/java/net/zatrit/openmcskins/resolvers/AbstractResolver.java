package net.zatrit.openmcskins.resolvers;

import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.util.NetworkUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public abstract class AbstractResolver<D extends AbstractResolver.IndexedPlayerData<?>> {
    protected static final Gson GSON = new Gson();

    public abstract D resolvePlayer(GameProfile player) throws IOException;

    public abstract static class IndexedPlayerData<TT> implements Serializable {
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
        public final IndexedPlayerData<TT> withIndex(int index) {
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

    public abstract static class MinecraftProfileTexturePlayerData extends IndexedPlayerData<MinecraftProfileTexture> {
        @Override
        public Identifier downloadTexture(MinecraftProfileTexture.Type type) {
            try {
                MinecraftProfileTexture texture = textures.get(type);
                if (type == MinecraftProfileTexture.Type.CAPE)
                    return NetworkUtils.downloadAndResize(new URL(texture.getUrl()).openStream(), texture.getUrl(), 2, 1);
                else return MinecraftClient.getInstance().getSkinProvider().loadSkin(texture, type);
            } catch (Exception e) {
                OpenMCSkins.handleError(e);
                return null;
            }
        }
    }
}
