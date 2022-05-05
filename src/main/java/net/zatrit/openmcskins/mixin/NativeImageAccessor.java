package net.zatrit.openmcskins.mixin;


import net.minecraft.client.texture.NativeImage;
import net.zatrit.openmcskins.annotation.KeepClass;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@KeepClass
@Mixin(NativeImage.class)
public interface NativeImageAccessor {
    @Accessor
    long getPointer();
}
