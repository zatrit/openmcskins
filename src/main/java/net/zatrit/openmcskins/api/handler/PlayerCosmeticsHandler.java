package net.zatrit.openmcskins.api.handler;

import net.zatrit.openmcskins.skins.CosmeticsParser;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PlayerCosmeticsHandler {
    @Nullable List<CosmeticsParser.CosmeticsItem> downloadCosmetics();
}
