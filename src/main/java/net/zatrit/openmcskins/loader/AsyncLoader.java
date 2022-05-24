package net.zatrit.openmcskins.loader;

import com.mojang.authlib.GameProfile;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import net.zatrit.openmcskins.interfaces.resolver.Resolver;
import net.zatrit.openmcskins.mod.OpenMCSkins;
import net.zatrit.openmcskins.resolvers.handler.AbstractPlayerHandler;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public record AsyncLoader(Function<Resolver<?>, Boolean> filter,
                          Function<List<? extends AbstractPlayerHandler<?>>, ?> processHandlers,
                          TriConsumer<Object, GameProfile, Object[]> doFinally) {
    public void loadAsync(GameProfile profile, Object... args) {
        List<? extends Resolver<?>> resolvers = OpenMCSkins.getResolvers().stream().filter(filter::apply).toList();
        Flowable.range(0, resolvers.size()).parallel().runOn(Schedulers.io()).mapOptional(i -> {
            Resolver<?> host = resolvers.get(i);
            return Optional.of(host.resolvePlayer(host.requiresUUID() ? PlayerRegistry.patchProfile(profile) : profile).withIndex(i));
        }).runOn(Schedulers.computation()).sequential().timeout(OpenMCSkins.getConfig().resolvingTimeout, TimeUnit.SECONDS).toList().doOnSuccess(handlers -> {
            Object result = processHandlers.apply(handlers);
            doFinally.accept(result, profile, args);
        }).doOnError(OpenMCSkins::handleError).subscribe();
    }
}
