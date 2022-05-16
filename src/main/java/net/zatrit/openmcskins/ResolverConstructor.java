package net.zatrit.openmcskins;

import net.zatrit.openmcskins.resolvers.Resolver;

@FunctionalInterface
public interface ResolverConstructor {
    Resolver<?> construct(String data);
}
