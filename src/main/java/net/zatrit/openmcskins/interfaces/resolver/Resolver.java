package net.zatrit.openmcskins.interfaces.resolver;

import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import net.zatrit.openmcskins.interfaces.handler.PlayerVanillaHandler;

import java.io.IOException;

@FunctionalInterface
public interface Resolver<D extends PlayerVanillaHandler> {
    Gson GSON = new Gson();

    D resolvePlayer(GameProfile profile) throws IOException;

    default boolean requiresUUID() {
        return true;
    }
}