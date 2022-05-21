package net.zatrit.openmcskins.mod.ears.mixin;

import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(PlayerEntityModel.class)
public class PlayerEntityModelMixin {
    @ModifyArgs(method = "getTexturedModelData", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPartBuilder;uv(II)Lnet/minecraft/client/model/ModelPartBuilder;", ordinal = 0))
    private static void moveEarsUv(@NotNull Args args) {
        args.set(0, 0);
    }

    @Redirect(method = "getTexturedModelData", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelPartBuilder;cuboid(FFFFFFLnet/minecraft/client/model/Dilation;)Lnet/minecraft/client/model/ModelPartBuilder;", ordinal = 0))
    private static ModelPartBuilder resizeCuboid(@NotNull ModelPartBuilder instance, float offsetX, float offsetY, float offsetZ, float sizeX, float sizeY, float sizeZ, Dilation extra) {
        return instance.cuboid(offsetX, offsetY, offsetZ, sizeX, sizeY, sizeZ, extra, 0.25F, 0.125F);
    }
}
