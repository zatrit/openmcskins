package net.zatrit.openmcskins.mixin;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.resources.ResourceLocation;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.TextureLoader;
import net.zatrit.openmcskins.annotation.KeepClass;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@KeepClass
@Mixin(PlayerInfo.class)
public abstract class PlayerInfoMixin {
    @Final
    @Shadow
    private Map<MinecraftProfileTexture.Type, ResourceLocation> textureLocations;

    @Shadow
    private boolean pendingTextures = false;

    @Shadow
    @Nullable
    private String skinModel;

    /**
     * @author zatrit
     * @reason I overwrote this because it might conflict with OpenMCSkins.
     */
    @Overwrite
    public void registerTextures() {
        if (!this.pendingTextures) {
            PlayerInfo info = (PlayerInfo) (Object) this;
            TextureLoader.resolve(info, (t, r, model) -> {
                this.textureLocations.put(t, r);
                this.skinModel = model;
            });
            this.pendingTextures = true;
        }
    }
}
