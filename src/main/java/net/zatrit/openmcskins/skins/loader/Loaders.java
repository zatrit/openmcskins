package net.zatrit.openmcskins.skins.loader;

import net.zatrit.openmcskins.skins.AsyncLoaderHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum Loaders {
    COSMETICS(new CosmeticsLoader()),
    VANILLA(new VanillaLoader());

    private final AsyncLoaderHandler handler;

    Loaders(@NotNull Loader loader) {
        this.handler = new AsyncLoaderHandler(loader);
    }

    @Contract(value = " -> new", pure = true)
    public @NotNull AsyncLoaderHandler getHandler() {
        return this.handler;
    }

}
