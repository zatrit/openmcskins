package net.zatrit.openmcskins.mod;

import com.chocohead.mm.api.ClassTinkerers;
import net.zatrit.openmcskins.annotation.KeepClass;
import net.zatrit.openmcskins.annotation.KeepClassMember;

@KeepClass
public class EarlyRiser implements Runnable {
    @KeepClassMember
    @Override
    public void run() {
        ClassTinkerers.enumBuilder("com/mojang/authlib/minecraft/MinecraftProfileTexture$Type", new Class[0]).addEnum(
                "EARS").build();
    }
}
