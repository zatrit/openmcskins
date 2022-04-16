package net.zatrit.openmcskins.mixin;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.TextureLoader;
import net.zatrit.openmcskins.annotation.KeepClass;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@KeepClass
@Mixin(PlayerListEntry.class)
public abstract class PlayerInfoMixin {
    @Final
    @Shadow
    private Map<MinecraftProfileTexture.Type, Identifier> textures;

    @Shadow
    private boolean texturesLoaded = false;

    @Shadow
    @Nullable
    private String model;

    /**
     * @author zatrit
     * @reason I overwrote this because it might conflict with OpenMCSkins.
     */
    @Overwrite
    public void loadTextures() {
        if (!this.texturesLoaded) {
            PlayerListEntry info = (PlayerListEntry) (Object) this;
            TextureLoader.resolve(info, (t, r, model) -> {
                this.textures.put(t, r);
                this.model = model;
            });
            this.texturesLoaded = true;
        }
    }
}
