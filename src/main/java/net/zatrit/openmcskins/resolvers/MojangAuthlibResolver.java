package net.zatrit.openmcskins.resolvers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.TextureLoader;
import org.jetbrains.annotations.NotNull;

public class MojangAuthlibResolver extends AbstractResolver<MojangAuthlibResolver.PlayerData> {

    @Override
    public PlayerData resolvePlayer(GameProfile profile) {
        return new PlayerData(profile);
    }

    public static class PlayerData extends IndexedPlayerData<MinecraftProfileTexture> {
        public PlayerData(@NotNull GameProfile profile) {
            if (profile.getProperties().isEmpty())
                TextureLoader.getSessionService().fillProfileProperties(profile, true);
            PlayerData.this.textures.putAll(TextureLoader.getSessionService().getTextures(profile, true));
            if (PlayerData.this.textures.containsKey(MinecraftProfileTexture.Type.SKIN))
                PlayerData.this.setModel(textures.get(MinecraftProfileTexture.Type.SKIN).getMetadata("model"));
        }

        @Override
        public Identifier downloadTexture(MinecraftProfileTexture.Type type) {
            MinecraftProfileTexture texture = this.textures.get(type);
            return MinecraftClient.getInstance().getSkinProvider().loadSkin(texture, type);
        }
    }
}
