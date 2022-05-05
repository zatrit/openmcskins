package net.zatrit.openmcskins.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.zatrit.openmcskins.annotation.KeepClass;
import org.jetbrains.annotations.ApiStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@KeepClass
@Mixin(AbstractClientPlayerEntity.class)
public interface AbstractClientPlayerEntityAccessor {
    @Invoker("getPlayerListEntry")
    PlayerListEntry invokeGetPlayerListEntry();
}
