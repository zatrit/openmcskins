package net.zatrit.openmcskins.io;

import net.zatrit.openmcskins.annotation.KeepClass;

@KeepClass
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
