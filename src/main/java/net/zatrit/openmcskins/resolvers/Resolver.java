package net.zatrit.openmcskins.resolvers;

import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import net.zatrit.openmcskins.resolvers.handler.PlayerHandler;

import java.io.IOException;

@FunctionalInterface
public interface Resolver<D extends PlayerHandler<?>> {
    Gson GSON = new Gson();

    D resolvePlayer(GameProfile profile) throws IOException;

    default boolean requiresUUID() {
        return true;
    }
}