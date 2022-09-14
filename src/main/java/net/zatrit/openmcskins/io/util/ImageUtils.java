package net.zatrit.openmcskins.io.util;

import com.google.common.math.IntMath;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;

public final class ImageUtils {
    private ImageUtils() {
    }

    @SuppressWarnings("UnstableApiUsage")
    public static @NotNull BufferedImage resizeToAspects(@NotNull BufferedImage source,
                                                         int width,
                                                         int height,
                                                         boolean flushSource) {
        int newHeight = IntMath.ceilingPowerOfTwo(Math.max(source.getHeight(), source.getWidth() / 2));
        final int relation = width / height;

        final BufferedImage target = new BufferedImage(newHeight * relation, newHeight, BufferedImage.TYPE_INT_ARGB);

        final Graphics2D graphics2D = (Graphics2D) target.getGraphics();
        graphics2D.drawImage(source, 0, 0, null);

        graphics2D.dispose();
        if (flushSource) {
            source.flush();
        }

        return target;
    }

    public static @Nullable Identifier registerNativeImage(@NotNull NativeImage image, String prefix) {
        NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
        if (TextureUtils.isValidDynamicTexture(texture)) {
            return MinecraftClient.getInstance().getTextureManager().registerDynamicTexture(prefix, texture);
        }
        return null;
    }
}
