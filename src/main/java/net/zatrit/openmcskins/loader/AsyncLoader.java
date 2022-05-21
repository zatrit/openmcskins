package net.zatrit.openmcskins.loader;

import com.mojang.authlib.GameProfile;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import net.zatrit.openmcskins.mod.OpenMCSkins;
import net.zatrit.openmcskins.interfaces.resolver.Resolver;
import net.zatrit.openmcskins.resolvers.handler.AbstractPlayerHandler;
import net.zatrit.openmcskins.interfaces.handler.PlayerVanillaHandler;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public record AsyncLoader(Function<Resolver<?>, Boolean> filter,
                          Function<List<? extends PlayerVanillaHandler>, ?> processHandlers,
                          TriConsumer<Object, Object, GameProfile> doFinally) {
    public void loadAsync(GameProfile profile, Object callback) {
        List<? extends Resolver<?>> resolvers = OpenMCSkins.getResolvers().stream().filter(filter::apply).toList();
        Flowable.range(0, resolvers.size()).parallel().runOn(Schedulers.io()).mapOptional(i -> {
            Resolver<?> host = resolvers.get(i);
            return Optional.of(host.resolvePlayer(host.requiresUUID() ? PlayerManager.patchProfile(profile) : profile).<AbstractPlayerHandler<?>>withIndex(i));
        }).runOn(Schedulers.computation()).sequential().timeout(OpenMCSkins.getConfig().resolvingTimeout, TimeUnit.SECONDS).toList().doOnSuccess(handlers -> {
            Object result = processHandlers.apply(handlers);
            doFinally.accept(result, callback, profile);
        }).doOnError(OpenMCSkins::handleError).subscribe();
    }
}
