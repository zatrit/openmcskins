package net.zatrit.openmcskins.resolvers;

import com.mojang.authlib.GameProfile;
import net.zatrit.openmcskins.resolvers.handler.PlayerCosmeticsHandler;

import java.io.IOException;

public interface PlayerCosmeticsResolver<D extends PlayerCosmeticsHandler> {
    D resolvePlayer(GameProfile profile) throws IOException;
}
