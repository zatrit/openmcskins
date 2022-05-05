package net.zatrit.openmcskins.mixin;

import com.google.common.hash.HashFunction;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.texture.PlayerSkinProvider;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.annotation.KeepClass;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@KeepClass
@Mixin(value = {PlayerSkinProvider.class, AbstractClientPlayerEntity.class})
public class HashingFunctionChangerMixin {
    @Redirect(method = "*", at = @At(value = "INVOKE", target = "Lcom/google/common/hash/Hashing;sha1()Lcom/google/common/hash/HashFunction;"))
    public HashFunction changeHashingAlgorithm() {
        return OpenMCSkins.getHashFunction();
    }
}
