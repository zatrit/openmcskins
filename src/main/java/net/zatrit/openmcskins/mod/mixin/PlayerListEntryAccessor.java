package net.zatrit.openmcskins.mod.mixin;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(PlayerListEntry.class)
public interface PlayerListEntryAccessor {
    @Accessor("texturesLoaded")
    void setTexturesLoaded(boolean texturesLoaded);

    @Accessor("textures")
    Map<MinecraftProfileTexture.Type, Identifier> getTextures();

    @Invoker("loadTextures")
    void invokeLoadTextures();
}
