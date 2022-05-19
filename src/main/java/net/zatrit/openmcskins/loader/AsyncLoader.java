package net.zatrit.openmcskins.loader;

import com.mojang.authlib.GameProfile;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.PlayerManager;
import net.zatrit.openmcskins.resolvers.Resolver;
import net.zatrit.openmcskins.resolvers.handler.PlayerHandler;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public record AsyncLoader(Function<Resolver<?>, Boolean> filter, Function<List<PlayerHandler<?>>, ?> processing,
                          TriConsumer<Object, Object, GameProfile> doFinally) {
    public void loadAsync(GameProfile profile, Object callback) {
        List<? extends Resolver<?>> resolvers = OpenMCSkins.getResolvers().stream().filter(filter::apply).toList();
        List<PlayerHandler<?>> handlers = new ArrayList<>();
        Flowable.range(0, resolvers.size()).parallel().mapOptional(i -> {
            Resolver<?> host = resolvers.get(i);
            return Optional.of(host.resolvePlayer(host.requiresUUID() ? PlayerManager.patchProfile(profile) : profile));
        }).runOn(Schedulers.io()).sequential().timeout(OpenMCSkins.getConfig().resolvingTimeout, TimeUnit.SECONDS).doOnNext(handlers::add).doFinally(() -> {
            Object result = processing.apply(handlers);
            doFinally.accept(result, callback, profile);
        }).doOnError(OpenMCSkins::handleError).subscribe();
    }
}
