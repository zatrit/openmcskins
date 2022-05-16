package net.zatrit.openmcskins.resolvers.handler;

import net.zatrit.openmcskins.loader.CosmeticsLoader;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PlayerCosmeticsHandler {
    @Nullable
    List<CosmeticsLoader.CosmeticsItem> downloadCosmetics();
}
