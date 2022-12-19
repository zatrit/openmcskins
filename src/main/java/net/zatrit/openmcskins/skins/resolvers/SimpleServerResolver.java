package net.zatrit.openmcskins.skins.resolvers;

import com.google.common.base.MoreObjects;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.zatrit.openmcskins.api.resolver.Resolver;
import net.zatrit.openmcskins.operators.RefillProfiles;
import net.zatrit.openmcskins.skins.resolvers.handler.AnimatedPlayerHandler;
import net.zatrit.openmcskins.util.NetworkUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public final class SimpleServerResolver implements Resolver<SimpleServerResolver.PlayerHandler> {
    private final String host;
    private final String format;
    private @Nullable RefillProfiles refillProfiles;

    public SimpleServerResolver(String host) {
        this(host, "%s/textures/%s");
    }

    public SimpleServerResolver(String host, String format) {
        this(host, format, null);
    }

    public SimpleServerResolver(String host,
                                String format,
                                @Nullable RefillProfiles refillProfiles) {
        this.host = NetworkUtils.fixUrl(host);
        this.format = format;
        this.refillProfiles = refillProfiles;
    }

    @Override
    public boolean requiresUUID() {
        return false;
    }

    @Override
    public @NotNull SimpleServerResolver.PlayerHandler resolvePlayer(@NotNull GameProfile profile) throws
            IOException {
        // Example: http://127.0.0.1:8080/textures/PlayerName
        final URL url = new URL(String.format(format,
                this.host,
                this.refillProfiles == null ?
                        profile.getName() :
                        profile.getId().toString()));
        final BufferedReader in = new BufferedReader(new InputStreamReader(
                url.openStream()));
        final Map<String, Map<String, Object>> map = GSON.<Map<String, Map<String, Object>>>fromJson(
                in,
                Map.class);

        return new PlayerHandler(map);
    }


    public static class PlayerHandler extends AnimatedPlayerHandler {
        @SuppressWarnings("unchecked")
        public PlayerHandler(Map<String, Map<String, Object>> data) {
            if (data != null) {
                data.forEach((k, v) -> {
                    final MinecraftProfileTexture.Type type = MinecraftProfileTexture.Type.valueOf(
                            k);
                    final Map<String, ?> metadata = (Map<String, ?>) MoreObjects.firstNonNull(
                            v.get("metadata"),
                            new HashMap<>());

                    if (v.containsKey("url")) {
                        this.textures.put(type, (String) v.get("url"));
                    }
                    if (metadata.containsKey("model")) {
                        this.setModel((String) metadata.get("model"));
                    }
                    if (metadata.containsKey("animated")) {
                        this.setAnimated(type,
                                (boolean) metadata.get("animated"));
                    }

                    metadata.clear();
                });
            }
        }
    }
}
