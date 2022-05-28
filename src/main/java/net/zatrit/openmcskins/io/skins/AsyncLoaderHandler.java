package net.zatrit.openmcskins.io.skins;

import com.mojang.authlib.GameProfile;
import net.zatrit.openmcskins.api.resolver.Resolver;
import net.zatrit.openmcskins.mod.OpenMCSkins;
import net.zatrit.openmcskins.io.skins.resolvers.handler.AbstractPlayerHandler;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record AsyncLoaderHandler(Function<Resolver<?>, Boolean> filter,
                                 Function<List<? extends AbstractPlayerHandler<?>>, ?> processHandlers,
                                 TriConsumer<Object, GameProfile, Object[]> doFinally,
                                 ExecutorService executor) {
    public AsyncLoaderHandler(Function<Resolver<?>, Boolean> filter,
                              Function<List<? extends AbstractPlayerHandler<?>>, ?> processHandlers,
                              TriConsumer<Object, GameProfile, Object[]> doFinally) {
        this(filter, processHandlers, doFinally, new ScheduledThreadPoolExecutor(127));
    }

    public void loadAsync(GameProfile profile, Object... args) {
        List<? extends Resolver<?>> resolvers = OpenMCSkins.getResolvers().stream().filter(filter::apply).toList();

        all(IntStream.range(0, resolvers.size()).boxed().map(i -> CompletableFuture.supplyAsync(() -> {
            try {
                Resolver<?> host = resolvers.get(i);
                if (!filter.apply(host)) return null;
                return host.resolvePlayer(host.requiresUUID() ? PlayerRegistry.patchProfile(profile) : profile).withIndex(i);
            } catch (IOException e) {
                OpenMCSkins.handleError(e);
                return null;
            }
        }, executor).orTimeout(OpenMCSkins.getConfig().resolvingTimeout, TimeUnit.SECONDS)).toList()).whenCompleteAsync((handlers, error) -> {
            if (error != null) OpenMCSkins.handleError(error);
            else {
                Object result = processHandlers.apply(handlers.stream().filter(Objects::nonNull).toList());
                doFinally.accept(result, profile, args);
            }
        });
    }

    // https://stackoverflow.com/a/36261808/12245612
    @SuppressWarnings("rawtypes")
    private static <T> CompletableFuture<List<T>> all(@NotNull List<CompletableFuture<T>> futures) {
        CompletableFuture[] cfs = futures.toArray(new CompletableFuture[0]);

        return CompletableFuture.allOf(cfs).thenApply(ignored -> {
            try {
                return futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
            } catch (Throwable e) {
                OpenMCSkins.handleError(e);
                return null;
            }
        });
    }
}
