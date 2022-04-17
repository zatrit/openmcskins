package net.zatrit.openmcskins.resolvers;

import com.google.gson.Gson;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public abstract class AbstractResolver<D extends AbstractResolver.IndexedPlayerData<?>> {
    protected static final Gson GSON = new Gson();

    public abstract D resolvePlayer(PlayerListEntry player) throws IOException;

    public abstract String getName();

    public abstract static class IndexedPlayerData<TT> implements Serializable {
        protected final Map<MinecraftProfileTexture.Type, TT> textures = new HashMap();
        public String model = "default";
        private int index;

        public abstract Identifier downloadTexture(MinecraftProfileTexture.Type type);

        public String getModelOrDefault() {
            return firstNonNull(model, "default");
        }

        public final IndexedPlayerData withIndex(int index) {
            this.index = index;
            return this;
        }

        public boolean hasTexture(MinecraftProfileTexture.Type type) {
            return textures.containsKey(type);
        }

        public int getIndex() {
            return index;
        }
    }
}
