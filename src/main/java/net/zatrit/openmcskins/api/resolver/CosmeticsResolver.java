package net.zatrit.openmcskins.api.resolver;

import net.zatrit.openmcskins.api.handler.PlayerCosmeticsHandler;
import net.zatrit.openmcskins.io.skins.resolvers.handler.AbstractPlayerHandler;

public interface CosmeticsResolver<T extends AbstractPlayerHandler<?> & PlayerCosmeticsHandler> extends Resolver<T> {
}
