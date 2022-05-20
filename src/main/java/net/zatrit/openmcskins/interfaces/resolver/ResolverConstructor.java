package net.zatrit.openmcskins.interfaces.resolver;

@FunctionalInterface
public interface ResolverConstructor {
    Resolver<?> construct(String data);
}
