package net.zatrit.openmcskins.interfaces.resolver;

import net.zatrit.openmcskins.interfaces.handler.PlayerCosmeticsHandler;
import net.zatrit.openmcskins.resolvers.handler.AbstractPlayerHandler;

public interface CosmeticsResolver<T extends AbstractPlayerHandler<?> & PlayerCosmeticsHandler> extends Resolver<T> {
}
