package net.zatrit.openmcskins.mod.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.zatrit.openmcskins.io.LocalAssetsCache;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(@NotNull RunArgs args, CallbackInfo ci) {
        LocalAssetsCache.setCacheRoot(args.directories.assetDir);
    }
}
