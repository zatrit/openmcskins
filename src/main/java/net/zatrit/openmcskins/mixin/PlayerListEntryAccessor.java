package net.zatrit.openmcskins.mixin;

import net.minecraft.client.network.PlayerListEntry;
import net.zatrit.openmcskins.annotation.KeepClass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@KeepClass
@Mixin(PlayerListEntry.class)
public interface PlayerListEntryAccessor {
    @Accessor("texturesLoaded")
    void setTexturesLoaded(boolean texturesLoaded);
}
