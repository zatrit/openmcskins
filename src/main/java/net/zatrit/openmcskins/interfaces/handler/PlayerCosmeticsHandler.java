package net.zatrit.openmcskins.interfaces.handler;

import net.zatrit.openmcskins.loader.Cosmetics;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PlayerCosmeticsHandler {
    @Nullable List<Cosmetics.CosmeticsItem> downloadCosmetics();
}
