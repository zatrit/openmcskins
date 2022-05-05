package net.zatrit.openmcskins.mixin;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.zatrit.openmcskins.OpenMCSkins;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static net.minecraft.util.math.MathHelper.isPowerOfTwo;

@Mixin(PlayerSkinTexture.class)
public abstract class PlayerSkinTextureMixin {
    private int realWidth = 0;

    @Contract(mutates = "this")
    @Redirect(method = "remapTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;getWidth()I"))
    private int getWidth(@NotNull NativeImage instance) {
        this.realWidth = instance.getWidth();
        return 64;
    }

    @Contract(mutates = "this")
    @Redirect(method = "remapTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;getHeight()I"))
    private int getHeight(@NotNull NativeImage instance) {
        return 64 / (instance.getWidth() / instance.getHeight());
    }

    @ModifyArgs(method = "remapTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/PlayerSkinTexture;stripColor(Lnet/minecraft/client/texture/NativeImage;IIII)V"))
    private void stripColor(@NotNull Args args) {
        processArgs(args, 4, 1);
    }

    @ModifyArgs(method = "remapTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/PlayerSkinTexture;stripAlpha(Lnet/minecraft/client/texture/NativeImage;IIII)V"))
    private void stripAlpha(@NotNull Args args) {
        processArgs(args, 4, 1);
    }

    @ModifyArgs(method = "remapTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;fillRect(IIIII)V"))
    private void fillRect(@NotNull Args args) {
        processArgs(args, 4, 0);
    }

    @ModifyArgs(method = "remapTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;copyRect(IIIIIIZZ)V"))
    private void copyRect(Args args) {
        processArgs(args, 6, 0);
    }

    @ModifyArgs(method = "remapTexture", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/texture/NativeImage;<init>(IIZ)V"))
    private void initNativeImage(Args args) {
        processArgs(args, 2, 0);
    }

    @Inject(method = "remapTexture", at = @At("HEAD"), cancellable = true)
    private void remapTexture(@NotNull NativeImage image, @NotNull CallbackInfoReturnable<NativeImage> cir) {
        int width = image.getWidth();
        int height = image.getHeight();
        if ((width != height * 2 && width != height) || !isPowerOfTwo(height)) {
            OpenMCSkins.LOGGER.info("Invalid skin texture resolution!");
            cir.setReturnValue(null);
        }
    }

    private void processArgs(Args args, int length, int offset) {
        for (int i = offset; i < offset + length; i++)
            args.set(i, ((int) args.get(i)) * (realWidth >> 6));
    }
}
