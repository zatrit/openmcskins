package net.zatrit.openmcskins.util.io;

import com.google.common.math.IntMath;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.mixin.NativeImageAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public final class ImageUtils {
    private ImageUtils() {
    }

    public static @NotNull NativeImage bufferedToNative(BufferedImage source) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(source, "png", outputStream);
        source.flush();
        return NativeImage.read(new ByteArrayInputStream(outputStream.toByteArray()));
    }

    public static @NotNull BufferedImage resizeToAspects(@NotNull BufferedImage source, int width, int height, boolean flushSource) {
        int newHeight = height;
        int relation = width / height;

        if (relation != source.getWidth() / source.getHeight() || !IntMath.isPowerOfTwo(source.getHeight()))
            while (newHeight < source.getHeight() || newHeight * relation < source.getWidth())
                newHeight *= 2;
        else newHeight = source.getHeight();

        BufferedImage target = new BufferedImage(newHeight * relation, newHeight, BufferedImage.TYPE_INT_ARGB);

        Graphics2D graphics2D = (Graphics2D) target.getGraphics();
        graphics2D.drawImage(source, 0, 0, null);

        graphics2D.dispose();
        if (flushSource) source.flush();

        return target;
    }

    public static @Nullable Identifier registerNativeImage(@NotNull NativeImage image, String prefix) {
        if (NativeImageAccessor.class.cast(image).getPointer() == 0L) return null;
        NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
        if (texture.getImage() == null) return null;
        return MinecraftClient.getInstance().getTextureManager().registerDynamicTexture(prefix, texture);
    }
}
