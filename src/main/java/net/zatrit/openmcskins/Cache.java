package net.zatrit.openmcskins;

import net.zatrit.openmcskins.util.io.LocalAssetsCache;

public enum Cache {
    MODELS("models"),
    SKINS("skins");

    private final LocalAssetsCache cache;

    Cache(String type) {
        this.cache = new LocalAssetsCache(type);
    }

    public LocalAssetsCache getCache() {
        return cache;
    }
}
