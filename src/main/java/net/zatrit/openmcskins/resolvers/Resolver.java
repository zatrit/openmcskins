package net.zatrit.openmcskins.resolvers;

import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import net.zatrit.openmcskins.resolvers.data.IndexedPlayerData;

import java.io.IOException;

public interface Resolver<D extends IndexedPlayerData<?>> {
    Gson GSON = new Gson();

    D resolvePlayer(GameProfile profile) throws IOException;
}