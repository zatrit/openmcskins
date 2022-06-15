package net.zatrit.openmcskins.io.skins.resolvers;

import com.google.gson.internal.LinkedTreeMap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.api.handler.PlayerCosmeticsHandler;
import net.zatrit.openmcskins.api.resolver.CosmeticsResolver;
import net.zatrit.openmcskins.io.Cache;
import net.zatrit.openmcskins.io.skins.CosmeticsParser;
import net.zatrit.openmcskins.io.util.NetworkUtils;
import net.zatrit.openmcskins.io.util.TextureUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.*;

public final class OptifineResolver implements CosmeticsResolver<OptifineResolver.PlayerSkinHandler> {
    private final String baseUrl;
    private final Map<String, CosmeticsParser.CosmeticsItem> cosmeticsCache = new HashMap<>();

    public OptifineResolver(String baseUrl) {
        this.baseUrl = NetworkUtils.fixUrl(baseUrl);
    }

    @Contract("_ -> new")
    public @NotNull OptifineResolver.PlayerSkinHandler resolvePlayer(GameProfile profile) {
        return new PlayerSkinHandler(profile);
    }

    @Override
    public boolean requiresUUID() {
        return false;
    }

    public class PlayerSkinHandler extends DirectResolver.PlayerHandler implements PlayerCosmeticsHandler {
        private final GameProfile profile;

        public PlayerSkinHandler(@NotNull GameProfile profile) {
            super(String.format("%s/capes/%s.png", baseUrl, profile.getName()), profile, MinecraftProfileTexture.Type.CAPE);
            this.profile = profile;
        }

        private static LinkedTreeMap<String, Object> mapFromReader(Reader reader) {
            return GSON.<LinkedTreeMap<String, Object>>fromJson(reader, LinkedTreeMap.class);
        }

        private static void loadTextureFromUrl(String url, Identifier id) throws Exception {
            final NativeImage image = NativeImage.read(Cache.SKINS.getCache().getOrDownload(url, new URL(url)::openStream));
            final NativeImageBackedTexture texture = new NativeImageBackedTexture(image);
            if (TextureUtils.isValidDynamicTexture(texture))
                MinecraftClient.getInstance().getTextureManager().registerTexture(id, texture);
        }

        @SuppressWarnings("unchecked")
        @Override
        public List<CosmeticsParser.CosmeticsItem> downloadCosmetics() {
            try {
                final String urlString = baseUrl + "/users/" + profile.getName() + ".cfg";
                final URL realUrl = new URL(urlString);

                if (NetworkUtils.getResponseCode(urlString) == 200) {
                    final BufferedReader in = new BufferedReader(new InputStreamReader(realUrl.openStream()));
                    final LinkedTreeMap<String, Object> map = mapFromReader(in);
                    final List<Map<String, Object>> items = (List<Map<String, Object>>) map.get("items");
                    final List<CosmeticsParser.CosmeticsItem> cosmetics = new ArrayList<>();

                    items.stream().filter(item -> Objects.equals(item.get("active"), "true")).forEach(item -> {
                        final String modelType = (String) item.get("type");
                        cosmetics.add(cosmeticsCache.computeIfAbsent(modelType, k -> {
                            final Identifier textureId = new Identifier("cosmetics_texture", modelType);
                            final Identifier modelId = new Identifier("cosmetics_model", modelType);

                            try {
                                loadTextureFromUrl(baseUrl + "/" + item.get("texture"), textureId);
                                final URL modelUrl = new URL(baseUrl + "/" + item.get("model"));

                                final LinkedTreeMap<String, Object> model = mapFromReader(new InputStreamReader(Cache.MODELS.getCache().getOrDownload(modelType, modelUrl::openStream)));
                                return CosmeticsParser.parseJemCosmeticItem(textureId, modelId, model, modelType);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }));
                    });

                    return cosmetics;
                }
            } catch (Exception ex) {
                OpenMCSkins.handleError(Optional.of(ex));
            }
            return null;
        }
    }

    @Override
    public void clear() {
        cosmeticsCache.clear();
    }
}
