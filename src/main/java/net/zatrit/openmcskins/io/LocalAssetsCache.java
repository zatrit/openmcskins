package net.zatrit.openmcskins.io;

import it.unimi.dsi.fastutil.booleans.BooleanComparators;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.io.util.StreamConsumer;
import net.zatrit.openmcskins.io.util.StreamSupplier;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class LocalAssetsCache {
    private static File cacheRoot;
    private final Supplier<File> cacheDir;

    public LocalAssetsCache(String type) {
        this.cacheDir = () -> new File(cacheRoot, type);
    }

    public static void setCacheRoot(File cacheDir) {
        cacheRoot = cacheDir;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public @NotNull File getCacheFile(String name) {
        final String hash = OpenMCSkins.getHashFunction().hashUnencodedChars(name).toString();
        final File file = Paths.get(cacheDir.get().getPath(), hash.substring(0, 2), hash).toFile();
        file.getParentFile().mkdirs();
        return file;
    }

    @Contract("_, _ -> new")
    public @NotNull InputStream getOrDownload(String name, StreamSupplier download) throws Exception {
        return this.getOrDownload(name, stream -> {
            try (final InputStream inputStream = download.openStream()) {
                IOUtils.copy(inputStream, stream);
            }
        });
    }

    @Contract("_, _ -> new")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public @NotNull InputStream getOrDownload(String name, StreamConsumer download) throws Exception {
        final File cacheFile = getCacheFile(name);

        if (!cacheFile.isFile()) {
            cacheFile.getParentFile().mkdirs();
            try (final OutputStream stream = new FileOutputStream(cacheFile)) {
                download.apply(stream);
            }
        }

        return new FileInputStream(cacheFile);
    }

    @Contract("_ -> new")
    public @NotNull CompletableFuture<Void> clear(Runnable onEachFile) {
        final Comparator<Path> pathComparator = (a, b) -> BooleanComparators.OPPOSITE_COMPARATOR.compare(Files.isRegularFile(a), Files.isRegularFile(b));
        final Path cachePath = cacheDir.get().toPath();
        return CompletableFuture.runAsync(() -> {
            try {
                if (cacheDir.get().exists())
                    try (final Stream<Path> walk = Files.walk(cachePath).sorted(pathComparator)) {
                        walk.forEach(f -> {
                            try {
                                Files.delete(f);
                            } catch (IOException e) {
                                OpenMCSkins.handleError(e);
                            }
                            if (Files.isRegularFile(f)) onEachFile.run();
                        });
                    }
            } catch (IOException e) {
                OpenMCSkins.handleError(e);
            }
        });
    }

    public long size() {
        final Path cachePath = cacheDir.get().toPath();
        try {
            if (cacheDir.get().exists())
                try (final Stream<Path> walk = Files.walk(cachePath).filter(Files::isRegularFile)) {
                    return walk.count();
                }
        } catch (IOException e) {
            OpenMCSkins.handleError(e);
        }
        return 0;
    }
}
