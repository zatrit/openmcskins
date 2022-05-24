package net.zatrit.openmcskins.interfaces.resolver;

import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import net.zatrit.openmcskins.resolvers.handler.AbstractPlayerHandler;

import java.io.IOException;

@FunctionalInterface
public interface Resolver<D extends AbstractPlayerHandler<?>> {
    Gson GSON = new Gson();

    D resolvePlayer(GameProfile profile) throws IOException;

    default boolean requiresUUID() {
        return true;
    }
}