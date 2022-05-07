package net.zatrit.openmcskins.util;

import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderCall;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.zatrit.openmcskins.annotation.KeepClass;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@KeepClass
public class AnimatedTexture extends AbstractTexture {
    private final NativeImage[] frames;
    private final int[] ids;
    private long lastFrameTime;
    private int frameIndex = 0;

    public AnimatedTexture(InputStream source) throws IOException {
        BufferedImage sourceImage = ImageIO.read(source);
        int frameHeight = sourceImage.getWidth() / 2;
        int frameWidth = sourceImage.getWidth();
        int framesCount = sourceImage.getHeight() / frameHeight;
        frames = new NativeImage[framesCount];

        for (int i = 0; i < framesCount; i++) {
            BufferedImage frame = new BufferedImage(frameWidth, frameHeight, sourceImage.getType());

            int[] pixels = sourceImage.getRaster().getPixels(0, frameHeight * i, frameWidth, frameHeight, (int[]) null);
            frame.getRaster().setPixels(0, 0, frameWidth, frameHeight, pixels);
            frames[i] = ImageUtils.bufferedToNative(frame);
        }

        ids = new int[framesCount];
        RenderSystem.recordRenderCall(() -> {
            for (int i = 0; i < framesCount; i++) {
                NativeImage frame = frames[i];
                ids[i] = TextureUtil.generateTextureId();
                TextureUtil.prepareImage(ids[i], frame.getWidth(), frame.getHeight());
                frame.upload(0, 0, 0, false);
            }
        });

        lastFrameTime = System.currentTimeMillis();
        sourceImage.flush();
    }

    @Override
    public void load(ResourceManager manager) {
    }

    @Override
    public void bindTexture() {
        long time = System.currentTimeMillis();

        if (time - lastFrameTime > 100L) {
            lastFrameTime = time;
            frameIndex = (frameIndex + 1) % frames.length;
        }

        super.bindTexture();
    }

    @Override
    public void close() {
        this.clearGlId();

        for (NativeImage frame : frames)
            frame.close();
    }

    @Override
    public int getGlId() {
        RenderSystem.assertOnRenderThreadOrInit();
        return ids[frameIndex];
    }

    @Override
    public void clearGlId() {
        RenderCall clearId = () -> {
            for (int i = 0; i < ids.length; i++)
                if (ids[i] != -1) {
                    TextureUtil.releaseTextureId(ids[i]);
                    ids[i] = -1;
                }
        };

        if (RenderSystem.isOnRenderThread()) clearId.execute();
        else RenderSystem.recordRenderCall(clearId);
    }
}
