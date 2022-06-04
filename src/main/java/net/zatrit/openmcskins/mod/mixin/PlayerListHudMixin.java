package net.zatrit.openmcskins.mod.mixin;

import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.network.ClientConnection;
import net.zatrit.openmcskins.OpenMCSkins;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;isEncrypted()Z"))
    public boolean isEncrypted(@NotNull ClientConnection instance) {
        return instance.isEncrypted() || OpenMCSkins.getConfig().forceIcons;
    }
}
