package net.zatrit.openmcskins.mod.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.skins.loader.Loaders;
import net.zatrit.openmcskins.skins.loader.VanillaLoader;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(PlayerListEntry.class)
public abstract class PlayerListEntryMixin {
    @Final
    @Shadow
    private Map<MinecraftProfileTexture.Type, Identifier> textures;

    @Shadow
    private boolean texturesLoaded;

    @Shadow
    @Nullable
    private String model;

    @Shadow
    public abstract GameProfile getProfile();

    /**
     * @author zatrit
     * @reason I overwrote this because I have my own solution
     */
    @Overwrite
    public void loadTextures() {
        if (this.getProfile() != null && !this.texturesLoaded) {
            synchronized (this) {
                this.textures.clear();
                this.model = null;

                Loaders.VANILLA.getHandler().loadAsync(getProfile(),
                        (VanillaLoader.SkinResolveCallback) (t, r, model) -> {
                            if (r != null) {
                                this.textures.put(t, r);
                                if (t == MinecraftProfileTexture.Type.SKIN) {
                                    this.model = model;
                                }
                            }
                        });
                this.texturesLoaded = true;
            }
        }

    }
}
