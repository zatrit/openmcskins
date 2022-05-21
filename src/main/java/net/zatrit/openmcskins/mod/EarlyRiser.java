package net.zatrit.openmcskins.mod;

import com.chocohead.mm.api.ClassTinkerers;
import org.spongepowered.asm.mixin.Mixins;

@SuppressWarnings("unused")
public class EarlyRiser implements Runnable {
    @Override
    public void run() {
        Mixins.addConfiguration("openmcskins.ears.mixins.json");
        ClassTinkerers.enumBuilder("com/mojang/authlib/minecraft/MinecraftProfileTexture$Type").addEnum("EARS").build();
    }
}
