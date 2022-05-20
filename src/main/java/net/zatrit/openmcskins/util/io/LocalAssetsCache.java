package net.zatrit.openmcskins.util.io;

import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.interfaces.io.StreamAction;
import net.zatrit.openmcskins.interfaces.io.StreamOpener;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Paths;
import java.util.function.Supplier;

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
        String hash = OpenMCSkins.getHashFunction().hashUnencodedChars(name).toString();
        File file = Paths.get(cacheDir.get().getPath(), hash.substring(0, 2), hash).toFile();
        file.getParentFile().mkdirs();
        return file;
    }

    @Contract("_, _ -> new")
    public @NotNull InputStream getOrDownload(String name, StreamOpener download) throws Exception {
        return this.getOrDownload(name, stream -> {
            try (InputStream inputStream = download.openStream()) {
                IOUtils.copy(inputStream, stream);
            }
        });
    }

    @Contract("_, _ -> new")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public @NotNull InputStream getOrDownload(String name, StreamAction download) throws Exception {
        File cacheFile = getCacheFile(name);

        if (!cacheFile.isFile()) {
            cacheFile.getParentFile().mkdirs();
            try (OutputStream stream = new FileOutputStream(cacheFile)) {
                download.apply(stream);
            }
        }

        return new FileInputStream(cacheFile);
    }
}
