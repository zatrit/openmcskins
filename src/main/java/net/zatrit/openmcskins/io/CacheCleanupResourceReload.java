package net.zatrit.openmcskins.io;

import net.minecraft.resource.ResourceReload;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.annotation.KeepClassMember;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CacheCleanupResourceReload implements ResourceReload {
    private final CompletableFuture<Void> future;
    private final long count;
    private long loaded;

    public CacheCleanupResourceReload(@NotNull Supplier<Stream<LocalAssetsCache>> caches) {
        this.count = caches.get().mapToLong(LocalAssetsCache::size).sum();
        this.future = CompletableFuture.allOf(caches.get().map(localAssetsCache -> localAssetsCache.clear(() -> loaded++)).toArray(
                CompletableFuture[]::new));
    }

    @KeepClassMember
    @Override
    public CompletableFuture<?> whenComplete() {
        return this.future;
    }


    @KeepClassMember
    @Override
    public float getProgress() {
        if (loaded == 0) {
            return 1;
        }

        OpenMCSkins.LOGGER.info(loaded + "/" + count);
        return ((float) loaded) / ((float) count);
    }
}
