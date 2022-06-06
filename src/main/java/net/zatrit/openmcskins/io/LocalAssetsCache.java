package net.zatrit.openmcskins.io;

import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.io.util.StreamConsumer;
import net.zatrit.openmcskins.io.util.StreamSupplier;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Paths;
import java.util.function.Supplier;

public final class LocalAssetsCache {
    private static File cacheRoot;
    private final Supplier<File> cacheDir;
    private final String type;

    public LocalAssetsCache(String type) {
        this.cacheDir = () -> new File(cacheRoot, type);
        this.type = type;
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
            try (InputStream inputStream = download.openStream()) {
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
            try (OutputStream stream = new FileOutputStream(cacheFile)) {
                download.apply(stream);
            }
        }

        return new FileInputStream(cacheFile);
    }

    public void clear(Runnable onFinish) {
        new Thread(() -> {
            try {
                FileUtils.deleteDirectory(cacheDir.get());
            } catch (IOException e) {
                OpenMCSkins.handleError(e);
            }
            onFinish.run();
        }, "AsyncClear-" + type).start();
    }
}
