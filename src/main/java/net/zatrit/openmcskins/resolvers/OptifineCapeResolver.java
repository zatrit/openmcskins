package net.zatrit.openmcskins.resolvers;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.util.HTTPUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

// TODO fix HD optifine capes
public class OptifineCapeResolver extends AbstractResolver<OptifineCapeResolver.IndexedPlayerData> {
    @Override
    public IndexedPlayerData resolvePlayer(@NotNull PlayerListEntry player) {
        return new IndexedPlayerData(player.getProfile().getName());
    }

    public static class IndexedPlayerData extends AbstractResolver.IndexedPlayerData<MinecraftProfileTexture> {
        private static final String URL = "http://s.optifine.net/capes/%s.png";

        public IndexedPlayerData(String name) {
            String formattedUrl = String.format(URL, name);

            if (HTTPUtil.getResponseCode(formattedUrl) != 200) return;

            MinecraftProfileTexture texture = new MinecraftProfileTexture(formattedUrl, new HashMap<>());
            textures.put(MinecraftProfileTexture.Type.CAPE, texture);
        }

        @Override
        public Identifier downloadTexture(MinecraftProfileTexture.Type type) {
            MinecraftProfileTexture texture = textures.get(type);
            return MinecraftClient.getInstance().getSkinProvider().loadSkin(texture, MinecraftProfileTexture.Type.CAPE);
        }
    }
}
