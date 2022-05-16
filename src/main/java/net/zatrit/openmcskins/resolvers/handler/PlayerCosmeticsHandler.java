package net.zatrit.openmcskins.resolvers.handler;

import net.zatrit.openmcskins.resolvers.loader.CosmeticsManager;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PlayerCosmeticsHandler {
    @Nullable
    List<CosmeticsManager.CosmeticsItem> downloadCosmetics();
}
