package net.zatrit.openmcskins.mod.optional.mixin;

import com.chocohead.mm.api.ClassTinkerers;
import com.google.common.base.MoreObjects;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.entity.feature.Deadmau5FeatureRenderer;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.annotation.RequiresMod;
import net.zatrit.openmcskins.mod.mixin.AbstractClientPlayerEntityAccessor;
import net.zatrit.openmcskins.mod.mixin.PlayerListEntryAccessor;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@RequiresMod(all = "mm", any = {"fabricloader:>=0.14", "quilt_loader"})
@Mixin(Deadmau5FeatureRenderer.class)
public class Deadmau5FeatureRendererMixin {
    private static final Type earsType = ClassTinkerers.getEnum(Type.class, "EARS");

    @Redirect(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Ljava/lang/String;equals(Ljava/lang/Object;)Z", remap = false))
    public boolean earsEnabled(@NotNull String string, Object object) {
        return OpenMCSkins.getConfig().ears;
    }

    @Redirect(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;getSkinTexture()Lnet/minecraft/util/Identifier;"))
    public Identifier playerEarsInsteadOfSkin(AbstractClientPlayerEntity instance) {
        final PlayerListEntry entry = ((AbstractClientPlayerEntityAccessor) instance).invokeGetPlayerListEntry();
        ((PlayerListEntryAccessor) entry).invokeLoadTextures();
        final Identifier ears = ((PlayerListEntryAccessor) entry).getTextures().get(earsType);
        return MoreObjects.firstNonNull(ears, entry.getSkinTexture());
    }

    @Redirect(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;hasSkinTexture()Z"))
    public boolean hasEars(AbstractClientPlayerEntity instance) {
        final PlayerListEntry entry = ((AbstractClientPlayerEntityAccessor) instance).invokeGetPlayerListEntry();
        if (entry == null) return false;
        ((PlayerListEntryAccessor) entry).invokeLoadTextures();
        return ((PlayerListEntryAccessor) entry).getTextures().containsKey(earsType);
    }
}
