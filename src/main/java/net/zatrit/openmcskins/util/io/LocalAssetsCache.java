package net.zatrit.openmcskins.util.io;

import net.zatrit.openmcskins.OpenMCSkins;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.function.Supplier;

public class LocalAssetsCache {
    private static File cacheRoot;
    private final Supplier<File> cacheDir;

    public LocalAssetsCache(String type) {
        this.cacheDir = () -> new File(cacheRoot, type);
    }

    public static void setCacheRoot(File cacheDir) {
        cacheRoot = cacheDir;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public File getCacheFile(String name) {
        String hash = OpenMCSkins.getHashFunction().hashUnencodedChars(name).toString();
        File file = Paths.get(cacheDir.get().getPath(), hash.substring(0, 2), hash).toFile();
        file.getParentFile().mkdirs();
        return file;
    }

    @Contract("_, _ -> new")
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public @NotNull InputStream getOrDownload(String name, StreamOpener download) throws Exception {
        File cacheFile = getCacheFile(name);
        cacheFile.getParentFile().mkdirs();

        if (!cacheFile.isFile()) IOUtils.copy(download.openStream(), new FileOutputStream(cacheFile));

        return new FileInputStream(cacheFile);
    }
}
