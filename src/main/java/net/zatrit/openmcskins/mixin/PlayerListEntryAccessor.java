package net.zatrit.openmcskins.mixin;

import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerListEntry.class)
public interface PlayerListEntryAccessor {
    @Accessor("texturesLoaded")
    void setTexturesLoaded(boolean texturesLoaded);
}
