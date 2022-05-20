package net.zatrit.openmcskins.interfaces.handler;

import net.zatrit.openmcskins.loader.Cosmetics;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PlayerCosmeticsHandler extends PlayerVanillaHandler {
    @Nullable List<Cosmetics.CosmeticsItem> downloadCosmetics();
}
