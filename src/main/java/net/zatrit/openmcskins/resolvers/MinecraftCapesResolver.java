package net.zatrit.openmcskins.resolvers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.util.NetworkUtils;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

public class MinecraftCapesResolver extends AbstractResolver<MinecraftCapesResolver.AnimatedPlayerData> {
    @Override
    public AnimatedPlayerData resolvePlayer(GameProfile profile) {
        return new AnimatedPlayerData(profile);
    }

    public static class AnimatedPlayerData extends IndexedPlayerData<String> {
        public AnimatedPlayerData(GameProfile profile) {
            this.textures.put(MinecraftProfileTexture.Type.CAPE, "https://minecraftcapes.net/profile/" + profile.getId().toString().replace("-", ""));
        }

        @Override
        public @Nullable Identifier downloadTexture(MinecraftProfileTexture.Type type) {
            try {
                String url = this.textures.get(type);
                Map<String, ?> map = GSON.<Map<String, ?>>fromJson(new InputStreamReader(new URL(url).openStream()), Map.class);
                Map<String, String> textures = (Map<String, String>) map.get("textures");
                boolean animated = (boolean) map.get("animatedCape");
                byte[] bytes = Base64.decodeBase64(textures.get("cape"));
                ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);

                if (animated) return NetworkUtils.loadAnimatedCape(inputStream, url);
                else return NetworkUtils.loadStaticCape(inputStream, url);
            } catch (Exception e) {
                OpenMCSkins.handleError(e);
                return null;
            }
        }

        @Override
        public boolean hasTexture(MinecraftProfileTexture.Type type) {
            if (this.textures.containsKey(type)) return NetworkUtils.getResponseCode(this.textures.get(type)) == 200;
            return false;
        }
    }
}
