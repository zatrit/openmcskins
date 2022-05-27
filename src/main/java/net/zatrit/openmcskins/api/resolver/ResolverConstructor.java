package net.zatrit.openmcskins.api.resolver;

@FunctionalInterface
public interface ResolverConstructor {
    Resolver<?> construct(String data);
}
