package net.zatrit.openmcskins;

import net.zatrit.openmcskins.resolvers.Resolver;

public interface ResolverConstructor {
    Resolver<?> construct(String data);
}
