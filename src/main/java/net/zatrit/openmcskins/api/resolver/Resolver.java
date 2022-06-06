package net.zatrit.openmcskins.api.resolver;

import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.Clearable;
import net.zatrit.openmcskins.io.skins.resolvers.handler.AbstractPlayerHandler;

import java.io.IOException;

public interface Resolver<D extends AbstractPlayerHandler<?>> extends Clearable {
    Gson GSON = new Gson();

    D resolvePlayer(GameProfile profile) throws IOException;

    default boolean requiresUUID() {
        return true;
    }

    @Override
    default void clear() {
    }
}