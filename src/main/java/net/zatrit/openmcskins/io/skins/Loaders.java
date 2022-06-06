package net.zatrit.openmcskins.io.skins;

import net.zatrit.openmcskins.io.skins.loader.CosmeticsLoader;
import net.zatrit.openmcskins.io.skins.loader.Loader;
import net.zatrit.openmcskins.io.skins.loader.VanillaLoader;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum Loaders {
    COSMETICS(new CosmeticsLoader()), VANILLA(new VanillaLoader());

    private final AsyncLoaderHandler handler;

    Loaders(@NotNull Loader loader) {
        this.handler = new AsyncLoaderHandler(loader);
    }

    @Contract(value = " -> new", pure = true)
    public @NotNull AsyncLoaderHandler getHandler() {
        return this.handler;
    }

}
