package net.zatrit.openmcskins.resolvers.handler;

import net.zatrit.openmcskins.loader.CosmeticsLoader;

import java.util.List;

public interface PlayerCosmeticsHandler {
    List<CosmeticsLoader.CosmeticsItem> downloadCosmetics();
}
