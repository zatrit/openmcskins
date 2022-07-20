package net.zatrit.openmcskins.render.textures;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderCall;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.resource.ResourceManager;
import net.zatrit.openmcskins.annotation.KeepClassMember;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.io.InputStream;

public class AnimatedTexture extends AbstractTexture {
    private final int[] ids;
    private final int framesCount;
    private long lastFrameTime;
    private int frameIndex = 0;

    public AnimatedTexture(InputStream source) throws IOException {
        final NativeImage sourceImage = NativeImage.read(source);
        final int frameHeight = sourceImage.getWidth() / 2;
        framesCount = sourceImage.getHeight() / frameHeight;

        ids = new int[framesCount];
        RenderSystem.recordRenderCall(() -> {
            GL11.glGenTextures(ids);

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
        final long time = System.currentTimeMillis();

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
        final RenderCall clearId = () -> {
            GlStateManager._deleteTextures(ids);
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
