package net.zatrit.openmcskins.util;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.mixin.PlayerSkinProviderAccessor;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.util.Objects;

public final class TextureUtils {
    private TextureUtils() {
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static @NotNull File getCacheFile(String name) {
        String hash = OpenMCSkins.getHashFunction().hashUnencodedChars(name).toString();
        File cacheFile = Path.of(((PlayerSkinProviderAccessor) MinecraftClient.getInstance().getSkinProvider()).getSkinCacheDir().getPath(), hash.substring(0, 2), hash).toFile();
        cacheFile.getParentFile().mkdirs();
        return cacheFile;
    }

    public static @Nullable Identifier loadStaticTexture(InputStream stream, String name, int @NotNull [] aspects, boolean cache) throws Exception {
        int width = aspects[0];
        int height = aspects[1];
        File cacheFile = getCacheFile(name);
        BufferedImage sourceImage;

        if (cache) {
            if (!cacheFile.isFile()) IOUtils.copy(stream, new FileOutputStream(cacheFile));
            sourceImage = ImageIO.read(new FileInputStream(cacheFile));
        } else sourceImage = ImageIO.read(stream);
        stream.close();

        BufferedImage resized = ImageUtils.resizeToAspects(sourceImage, width, height, true);
        if (cache) ImageIO.write(resized, "png", cacheFile);
        NativeImage nativeImage = ImageUtils.bufferedToNative(resized);
        return ImageUtils.registerNativeImage(nativeImage, cacheFile.getName());
    }

    public static @NotNull Identifier loadAnimatedTexture(InputStream stream, String name, boolean cache) throws IOException {
        File cacheFile = getCacheFile(name);
        AnimatedTexture animatedTexture;

        if (cache) {
            if (!cacheFile.isFile()) IOUtils.copy(stream, new FileOutputStream(cacheFile));
            animatedTexture = new AnimatedTexture(new FileInputStream(cacheFile));
        } else animatedTexture = new AnimatedTexture(stream);
        stream.close();

        Identifier id = new Identifier("animated/" + cacheFile.getName());
        MinecraftClient.getInstance().getTextureManager().registerTexture(id, animatedTexture);
        return id;
    }

    public static int[] getAspects(MinecraftProfileTexture.Type type) {
        return switch (type) {
            case SKIN -> new int[]{1, 1};
            case CAPE, ELYTRA -> new int[]{2, 1};
        };
    }

    @Contract(pure = true)
    public static @Nullable Identifier loadPlayerSkin(InputStream stream, String model, String textureUrl, boolean cacheEnabled) throws IOException {
        File cacheFile = getCacheFile(textureUrl);
        NativeImage nativeImage;

        if (!cacheFile.isFile()) {
            BufferedImage image = ImageIO.read(stream);
            BufferedImage target = ImageUtils.resizeToAspects(image, 1, 1, false);
            nativeImage = ImageUtils.bufferedToNative(target);

            int scale = image.getWidth() / 64;
            boolean isLegacy = image.getWidth() == image.getHeight() * 2;
            image.flush();

            if (isLegacy) {
                fixedFillRect(nativeImage, scale);

                fixedCopyRect(nativeImage, 4, 16, 16, 4, 4, scale);
                fixedCopyRect(nativeImage, 8, 16, 16, 4, 4, scale);
                fixedCopyRect(nativeImage, 0, 20, 24, 4, 12, scale);
                fixedCopyRect(nativeImage, 4, 20, 16, 4, 12, scale);
                fixedCopyRect(nativeImage, 8, 20, 8, 4, 12, scale);
                fixedCopyRect(nativeImage, 12, 20, 16, 4, 12, scale);
                fixedCopyRect(nativeImage, 44, 16, -8, 4, 4, scale);
                fixedCopyRect(nativeImage, 48, 16, -8, 4, 4, scale);
                fixedCopyRect(nativeImage, 40, 20, 0, 4, 12, scale);

                if (Objects.equals(model, "default")) {
                    fixedCopyRect(nativeImage, 44, 20, -8, 4, 12, scale);
                    fixedCopyRect(nativeImage, 48, 20, -16, 4, 12, scale);
                    fixedCopyRect(nativeImage, 52, 20, -8, 4, 12, scale);
                } else if (Objects.equals(model, "slim")) {
                    fixedCopyRect(nativeImage, 44, 20, -8, 3, 12, scale);
                    fixedCopyRect(nativeImage, 47, 20, -16, 4, 12, scale);
                    fixedCopyRect(nativeImage, 51, 20, -8, 3, 12, scale);
                }
            }

            if (cacheEnabled) nativeImage.writeTo(cacheFile);
        } else nativeImage = NativeImage.read(new FileInputStream(cacheFile));

        return ImageUtils.registerNativeImage(nativeImage, cacheFile.getName());
    }

    private static void fixedCopyRect(@NotNull NativeImage image, int x, int y, int translateX, int width, int height, int n) {
        image.copyRect(x * n, y * n, translateX * n, 32 * n, width * n, height * n, true, false);
    }

    private static void fixedFillRect(@NotNull NativeImage image, int n) {
        image.fillRect(0, 32 * n, 64 * n, 32 * n, 0);
    }
}
