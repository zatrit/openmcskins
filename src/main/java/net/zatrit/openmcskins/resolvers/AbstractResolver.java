package net.zatrit.openmcskins.resolvers;

import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public abstract class AbstractResolver<D extends AbstractResolver.IndexedPlayerData<?>> {
    protected static final Gson GSON = new Gson();

    public abstract D resolvePlayer(GameProfile profile) throws IOException;

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
}
