package net.zatrit.openmcskins.resolvers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.resolvers.handler.IndexedPlayerHandler;
import net.zatrit.openmcskins.util.PlayerManager;
import org.jetbrains.annotations.NotNull;

public class MojangAuthlibResolver implements Resolver<MojangAuthlibResolver.PlayerHandler> {
    @Override
    public PlayerHandler resolvePlayer(GameProfile profile) {
        return new PlayerHandler(profile);
    }

    public static class PlayerHandler extends IndexedPlayerHandler<MinecraftProfileTexture> {
        public PlayerHandler(@NotNull GameProfile profile) {
            if (profile.getProperties().isEmpty())
                PlayerManager.getSessionService().fillProfileProperties(profile, true);
            PlayerHandler.this.textures.putAll(PlayerManager.getSessionService().getTextures(profile, true));
            if (PlayerHandler.this.textures.containsKey(MinecraftProfileTexture.Type.SKIN))
                PlayerHandler.this.setModel(textures.get(MinecraftProfileTexture.Type.SKIN).getMetadata("model"));
        }

        @Override
        public Identifier downloadTexture(MinecraftProfileTexture.Type type) {
            MinecraftProfileTexture texture = this.textures.get(type);
            return MinecraftClient.getInstance().getSkinProvider().loadSkin(texture, type);
        }
    }
}
