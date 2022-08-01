package net.zatrit.openmcskins.io.skins.resolvers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.api.resolver.Resolver;
import net.zatrit.openmcskins.config.options.RefillProfiles;
import net.zatrit.openmcskins.io.skins.PlayerRegistry;
import net.zatrit.openmcskins.io.skins.resolvers.handler.AbstractPlayerHandler;
import org.jetbrains.annotations.NotNull;

public class MojangAuthlibResolver implements Resolver<MojangAuthlibResolver.PlayerHandler> {
    private final RefillProfiles refillProfiles;

    public MojangAuthlibResolver(RefillProfiles refillProfiles) {
        this.refillProfiles = refillProfiles;
    }

    @Override
    public PlayerHandler resolvePlayer(GameProfile profile) {
        return new PlayerHandler(profile);
    }

    public class PlayerHandler extends AbstractPlayerHandler<MinecraftProfileTexture> {
        public PlayerHandler(@NotNull GameProfile profile) {
            if (refillProfiles == RefillProfiles.REFILL_ALWAYS)
                profile.getProperties().clear();
            if (profile.getProperties().isEmpty() && refillProfiles != RefillProfiles.DONT_REFILL)
                PlayerRegistry.getSessionService().fillProfileProperties(profile, true);
            PlayerHandler.this.textures.putAll(PlayerRegistry.getSessionService().getTextures(profile, true));
            this.textures.forEach((type, texture) -> OpenMCSkins.LOGGER.info(type.name() + ": " + texture.getUrl() + ", " + texture.getHash()));
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
