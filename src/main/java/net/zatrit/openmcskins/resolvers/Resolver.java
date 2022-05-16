package net.zatrit.openmcskins.resolvers;

import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import net.zatrit.openmcskins.resolvers.handler.IndexedPlayerHandler;

import java.io.IOException;

@FunctionalInterface
public interface Resolver<D extends IndexedPlayerHandler<?>> {
    Gson GSON = new Gson();

    D resolvePlayer(GameProfile profile) throws IOException;
}