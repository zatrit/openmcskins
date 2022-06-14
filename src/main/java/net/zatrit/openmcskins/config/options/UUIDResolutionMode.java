package net.zatrit.openmcskins.config.options;

import com.mojang.authlib.minecraft.UserApiService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.zatrit.openmcskins.mod.mixin.MinecraftClientAccessor;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public enum UUIDResolutionMode {
    AUTO(() -> {
        final MinecraftClient client = MinecraftClient.getInstance();
        final ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();

        final boolean clientOffline = ((MinecraftClientAccessor) client).getUserApiService() == UserApiService.OFFLINE;
        final boolean serverOffline = networkHandler != null && !networkHandler.getConnection().isEncrypted();

        return clientOffline || serverOffline;
    }),
    NORMAL(() -> false),
    BY_NAME(() -> true);

    private final Supplier<Boolean> function;

    UUIDResolutionMode(Supplier<Boolean> checkOfflineMode) {
        this.function = checkOfflineMode;
    }

    public boolean shouldResolveByName() {
        return this.function.get();
    }
}
