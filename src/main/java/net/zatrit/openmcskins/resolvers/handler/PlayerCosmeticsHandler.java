package net.zatrit.openmcskins.resolvers.handler;

import net.zatrit.openmcskins.loader.Cosmetics;

import java.util.List;

public interface PlayerCosmeticsHandler {
    List<Cosmetics.CosmeticsItem> downloadCosmetics();
}
