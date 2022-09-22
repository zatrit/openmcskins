package net.zatrit.openmcskins.util;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.io.Cache;
import net.zatrit.openmcskins.mod.mixin.NativeImageAccessor;
import net.zatrit.openmcskins.render.textures.AnimatedTexture;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Objects;
import java.util.function.Supplier;

public final class TextureUtils {
    private TextureUtils() {
    }

    public static @Nullable Identifier loadStaticTexture(StreamSupplier sourceStream,
                                                         String name,
                                                         int @NotNull [] aspects,
                                                         boolean cache) throws Exception {
        int width = aspects[0];
        int height = aspects[1];

        NativeImage image = NativeImage.read(Cache.SKINS.getCache().getOrDownload(name, stream -> {
            BufferedImage sourceImage;

            try (final InputStream inputStream = sourceStream.openStream()) {
                sourceImage = ImageIO.read(inputStream);
            }

            sourceImage = ImageUtils.resizeToAspects(sourceImage, width, height, true);
            if (cache) {
                ImageIO.write(sourceImage, "png", stream);
            }
        }));

        return ImageUtils.registerNativeImage(image,
                String.valueOf(OpenMCSkins.getHashFunction().hashUnencodedChars(name)));
    }

    public static @NotNull Identifier loadAnimatedTexture(StreamSupplier sourceStream,
                                                          String name,
                                                          boolean cache) throws Exception {
        InputStream stream = cache ?
                Cache.SKINS.getCache().getOrDownload(name, sourceStream) :
                sourceStream.openStream();
        AnimatedTexture animatedTexture = new AnimatedTexture(stream);

        Identifier id = new Identifier("animated/" + OpenMCSkins.getHashFunction().hashUnencodedChars(name));
        MinecraftClient.getInstance().getTextureManager().registerTexture(id, animatedTexture);
        return id;
    }

    @Contract(pure = true)
    public static int @NotNull [] getAspects(MinecraftProfileTexture.Type type) {
        return type == MinecraftProfileTexture.Type.SKIN ? new int[]{1, 1} : new int[]{2, 1};
    }

    @Contract(pure = true)
    public static @Nullable Identifier loadPlayerSkin(Supplier<InputStream> sourceStream,
                                                      String model,
                                                      String textureUrl,
                                                      boolean cache) throws IOException {
        final File cacheFile = Cache.SKINS.getCache().getCacheFile(textureUrl);
        NativeImage nativeImage;

        if (cacheFile.isFile()) {
            nativeImage = NativeImage.read(new FileInputStream(cacheFile));
        } else {
            final BufferedImage image = ImageIO.read(sourceStream.get());
            final BufferedImage bufferedImage = ImageUtils.resizeToAspects(image, 1, 1, false);

            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", outputStream);
            bufferedImage.flush();
            nativeImage = NativeImage.read(new ByteArrayInputStream(outputStream.toByteArray()));

            final int scale = image.getWidth() / 64;
            final boolean isLegacy = image.getWidth() == image.getHeight() * 2;
            final boolean isValid = isLegacy || image.getWidth() == image.getHeight();
            if (!isValid) {
                OpenMCSkins.LOGGER.warn("Invalid image resolution");
                return null;
            }
            image.flush();

            if (isLegacy) {
                nativeImage.fillRect(0, 32 * scale, 64 * scale, 32 * scale, 0);

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

            if (cache) {
                nativeImage.writeTo(cacheFile);
            }
        }

        return ImageUtils.registerNativeImage(nativeImage, cacheFile.getName());
    }

    private static void fixedCopyRect(@NotNull NativeImage image,
                                      int x,
                                      int y,
                                      int translateX,
                                      int width,
                                      int height,
                                      int n) {
        image.copyRect(x * n, y * n, translateX * n, 32 * n, width * n, height * n, true, false);
    }

    @SuppressWarnings("ConstantConditions")
    public static boolean isValidDynamicTexture(@NotNull NativeImageBackedTexture texture) {
        return texture.getImage() != null && NativeImageAccessor.class.cast(texture.getImage()).getPointer() != 0L;
    }
}
