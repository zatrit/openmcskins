package net.zatrit.openmcskins.resolvers.handler;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.util.io.TextureUtils;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public abstract class AnimatedTexturePlayerHandler extends IndexedPlayerHandler<String> {
    private final Map<MinecraftProfileTexture.Type, Boolean> animated = new HashMap<>();

    protected boolean isAnimated(MinecraftProfileTexture.Type type) {
        return animated.getOrDefault(type, false);
    }

    protected final void setAnimated(MinecraftProfileTexture.Type type, boolean value) {
        animated.put(type, value);
    }

    protected InputStream openStream(String path, MinecraftProfileTexture.Type type) {
        try {
            return new URL(path).openStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean cacheEnabled() {
        return true;
    }

    @Override
    public @Nullable Identifier downloadTexture(MinecraftProfileTexture.Type type) {
        try {
            String textureUrl = textures.get(type);

            if (isAnimated(type) && OpenMCSkins.getConfig().ignoreAnimatedCapes) return null;

            if (type == MinecraftProfileTexture.Type.SKIN)
                return TextureUtils.loadPlayerSkin(() -> openStream(textureUrl, type), getModelOrDefault(), textureUrl, this.cacheEnabled());
            else if (isAnimated(type))
                return TextureUtils.loadAnimatedTexture(() -> openStream(textureUrl, type), textureUrl, this.cacheEnabled());
            return TextureUtils.loadStaticTexture(() -> openStream(textureUrl, type), textureUrl, TextureUtils.getAspects(type), this.cacheEnabled());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}