package net.zatrit.openmcskins.mixin;

import net.minecraft.client.texture.PlayerSkinProvider;
import net.zatrit.openmcskins.annotation.KeepClass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.io.File;

@KeepClass
@Mixin(PlayerSkinProvider.class)
public interface PlayerSkinProviderAccessor {
    @Accessor("skinCacheDir")
    File getSkinCacheDir();
}
