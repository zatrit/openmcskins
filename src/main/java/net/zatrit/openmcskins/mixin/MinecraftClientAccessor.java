package net.zatrit.openmcskins.mixin;

import com.mojang.authlib.minecraft.UserApiService;
import net.minecraft.client.MinecraftClient;
import net.zatrit.openmcskins.annotation.KeepClass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@KeepClass
@Mixin(MinecraftClient.class)
public interface MinecraftClientAccessor {
    @Accessor
    UserApiService getUserApiService();
}
