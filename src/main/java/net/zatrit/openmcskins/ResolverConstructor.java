package net.zatrit.openmcskins;

import net.zatrit.openmcskins.resolvers.AbstractResolver;

public interface ResolverConstructor {
    AbstractResolver<?> construct(String data);
}
