package net.zatrit.openmcskins.mod.entry;

import com.chocohead.mm.api.ClassTinkerers;
import net.zatrit.openmcskins.annotation.KeepClass;

@KeepClass
public class EarlyRisers implements Runnable {
    @Override
    public void run() {
        ClassTinkerers.enumBuilder("com/mojang/authlib/minecraft/MinecraftProfileTexture$Type", new Class[0]).addEnum(
                "EARS").build();
    }
}
