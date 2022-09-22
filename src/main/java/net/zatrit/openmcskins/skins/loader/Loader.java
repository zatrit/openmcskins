package net.zatrit.openmcskins.skins.loader;

import com.mojang.authlib.GameProfile;
import net.zatrit.openmcskins.api.resolver.Resolver;
import net.zatrit.openmcskins.skins.resolvers.handler.AbstractPlayerHandler;

import java.util.List;

public interface Loader {
    boolean filter(Resolver<?> resolver);

    Object processHandlers(List<? extends AbstractPlayerHandler<?>> handlers);

    void doFinally(Object result, GameProfile profile, Object[] args);
}
