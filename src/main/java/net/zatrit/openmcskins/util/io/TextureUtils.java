package net.zatrit.openmcskins.util.io;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.render.textures.AnimatedTexture;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.function.Supplier;

public final class TextureUtils {
    private TextureUtils() {
    }

    public static @Nullable Identifier loadStaticTexture(StreamOpener sourceStream, String name, int @NotNull [] aspects, boolean cache) throws Exception {
        int width = aspects[0];
        int height = aspects[1];
        File cacheFile = OpenMCSkins.getSkinsCache().getCacheFile(name);
        BufferedImage image;

        if (cacheFile.isFile() && cache) image = ImageIO.read(new FileInputStream(cacheFile));
        else {
            BufferedImage sourceImage;

            sourceImage = ImageIO.read(sourceStream.openStream());
            sourceStream.openStream().close();

            image = ImageUtils.resizeToAspects(sourceImage, width, height, true);
            if (cache) ImageIO.write(image, "png", cacheFile);
        }

        NativeImage nativeImage = ImageUtils.bufferedToNative(image);
        return ImageUtils.registerNativeImage(nativeImage, cacheFile.getName());
    }

    public static @NotNull Identifier loadAnimatedTexture(StreamOpener sourceStream, String name, boolean cache) throws Exception {
        InputStream stream = cache ? OpenMCSkins.getSkinsCache().getOrDownload(name, sourceStream) : sourceStream.openStream();
        AnimatedTexture animatedTexture = new AnimatedTexture(stream);

        Identifier id = new Identifier("animated/" + OpenMCSkins.getHashFunction().hashUnencodedChars(name));
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
    public static @Nullable Identifier loadPlayerSkin(Supplier<InputStream> sourceStream, String model, String textureUrl, boolean cache) throws IOException {
        File cacheFile = OpenMCSkins.getSkinsCache().getCacheFile(textureUrl);
        NativeImage nativeImage;

        if (cacheFile.isFile()) {
            nativeImage = NativeImage.read(new FileInputStream(cacheFile));
        } else {
            BufferedImage image = ImageIO.read(sourceStream.get());
            BufferedImage target = ImageUtils.resizeToAspects(image, 1, 1, false);
            nativeImage = ImageUtils.bufferedToNative(target);

            int scale = image.getWidth() / 64;
            boolean isLegacy = image.getWidth() == image.getHeight() * 2;
            boolean isValid = isLegacy || image.getWidth() == image.getHeight();
            if (!isValid) {
                OpenMCSkins.LOGGER.warn("Invalid image resolution");
                return null;
            }
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

            if (cache) nativeImage.writeTo(cacheFile);
        }

        return ImageUtils.registerNativeImage(nativeImage, cacheFile.getName());
    }

    private static void fixedCopyRect(@NotNull NativeImage image, int x, int y, int translateX, int width, int height, int n) {
        image.copyRect(x * n, y * n, translateX * n, 32 * n, width * n, height * n, true, false);
    }

    private static void fixedFillRect(@NotNull NativeImage image, int n) {
        image.fillRect(0, 32 * n, 64 * n, 32 * n, 0);
    }
}
