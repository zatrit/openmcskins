package net.zatrit.openmcskins.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderCall;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.zatrit.openmcskins.annotation.KeepClassMember;

import java.io.IOException;
import java.io.InputStream;

public class AnimatedTexture extends AbstractTexture {
    private final int[] ids;
    private long lastFrameTime;
    private int frameIndex = 0;
    private final int framesCount;

    public AnimatedTexture(InputStream source) throws IOException {
        NativeImage sourceImage = NativeImage.read(source);
        int frameHeight = sourceImage.getWidth() / 2;
        framesCount = sourceImage.getHeight() / frameHeight;

        ids = new int[framesCount];
        RenderSystem.recordRenderCall(() -> {
            GlStateManager._genTextures(ids);

            for (int i = 0; i < framesCount; i++) {
                TextureUtil.prepareImage(ids[i], sourceImage.getWidth(), frameHeight);
                sourceImage.upload(0, 0, 0, 0, frameHeight * i, sourceImage.getWidth(), frameHeight, false, false);
            }

            sourceImage.close();
        });

        lastFrameTime = System.currentTimeMillis();
    }


    @KeepClassMember
    @Override
    public void load(ResourceManager manager) {
    }

    @KeepClassMember
    @Override
    public void bindTexture() {
        long time = System.currentTimeMillis();

        if (time - lastFrameTime > 100L) {
            lastFrameTime = time;
            frameIndex = (frameIndex + 1) % framesCount;
        }

        super.bindTexture();
    }

    @KeepClassMember
    @Override
    public void close() {
        this.clearGlId();
    }

    @KeepClassMember
    @Override
    public int getGlId() {
        RenderSystem.assertOnRenderThreadOrInit();
        return ids[frameIndex];
    }

    @KeepClassMember
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
