package net.zatrit.openmcskins.io.skins.resolvers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.api.resolver.Resolver;
import net.zatrit.openmcskins.io.skins.PlayerRegistry;
import net.zatrit.openmcskins.io.skins.resolvers.handler.AbstractPlayerHandler;
import org.jetbrains.annotations.NotNull;

public class MojangAuthlibResolver implements Resolver<MojangAuthlibResolver.PlayerHandler> {
    @Override
    public PlayerHandler resolvePlayer(GameProfile profile) {
        return new PlayerHandler(profile);
    }

    public static class PlayerHandler extends AbstractPlayerHandler<MinecraftProfileTexture> {
        public PlayerHandler(@NotNull GameProfile profile) {
            if (profile.getProperties().isEmpty())
                PlayerRegistry.getSessionService().fillProfileProperties(profile, true);
            PlayerHandler.this.textures.putAll(PlayerRegistry.getSessionService().getTextures(profile, true));
            if (PlayerHandler.this.textures.containsKey(MinecraftProfileTexture.Type.SKIN))
                PlayerHandler.this.setModel(textures.get(MinecraftProfileTexture.Type.SKIN).getMetadata("model"));
        }

        @Override
        public Identifier downloadTexture(MinecraftProfileTexture.Type type) {
            final MinecraftProfileTexture texture = this.textures.get(type);
            return MinecraftClient.getInstance().getSkinProvider().loadSkin(texture, type);
        }
    }
}
