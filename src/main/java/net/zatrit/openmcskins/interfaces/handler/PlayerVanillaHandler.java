package net.zatrit.openmcskins.interfaces.handler;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.interfaces.Indexable;
import org.jetbrains.annotations.Nullable;

public interface PlayerVanillaHandler extends Indexable {
    @Nullable Identifier downloadTexture(MinecraftProfileTexture.Type type);

    boolean hasTexture(MinecraftProfileTexture.Type type);

    String getModel();
}
