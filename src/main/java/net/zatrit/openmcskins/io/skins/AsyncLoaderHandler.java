package net.zatrit.openmcskins.io.skins;

import com.mojang.authlib.GameProfile;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.api.resolver.Resolver;
import net.zatrit.openmcskins.io.skins.loader.Loader;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public record AsyncLoaderHandler(Loader loader, ExecutorService executor) {
    public AsyncLoaderHandler(Loader loader) {
        this(loader, new ScheduledThreadPoolExecutor(127));
    }

    // https://stackoverflow.com/a/36261808/12245612
    @SuppressWarnings("rawtypes")
    private static <T> CompletableFuture<List<T>> all(@NotNull List<CompletableFuture<T>> futures) {
        final CompletableFuture[] cfs = futures.toArray(new CompletableFuture[0]);

        return CompletableFuture.allOf(cfs).thenApply(ignored -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }

    public void loadAsync(GameProfile profile, Object... args) {
        final Resolver<?>[] resolvers = Arrays.stream(OpenMCSkins.getResolvers()).filter(loader::filter).toArray(Resolver[]::new);

        all(IntStream.range(0, resolvers.length).boxed().map(i -> CompletableFuture.supplyAsync(() -> {
            try {
                Resolver<?> host = resolvers[i];
                if (!loader.filter(host)) return null;
                return host.resolvePlayer(host.requiresUUID() ? PlayerRegistry.patchProfile(profile) : profile).withIndex(i);
            } catch (Exception e) {
                OpenMCSkins.handleError(e);
                return null;
            }
        }, executor).orTimeout(OpenMCSkins.getConfig().resolvingTimeout, TimeUnit.SECONDS)).toList()).whenCompleteAsync((handlers, error) -> {
            if (error != null) OpenMCSkins.handleError(Optional.of(error));
            else {
                Object result = loader.processHandlers(handlers.stream().filter(Objects::nonNull).toList());
                loader.doFinally(result, profile, args);
            }
        });
    }
}
