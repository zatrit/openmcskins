package net.zatrit.openmcskins.skins.resolvers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.api.resolver.Resolver;
import net.zatrit.openmcskins.operators.RefillProfiles;
import net.zatrit.openmcskins.skins.PlayerRegistry;
import net.zatrit.openmcskins.skins.resolvers.handler.AbstractPlayerHandler;
import org.jetbrains.annotations.NotNull;

public class MojangAuthlibResolver implements Resolver<MojangAuthlibResolver.PlayerHandler> {
    private final RefillProfiles refillProfiles;

    public MojangAuthlibResolver(RefillProfiles refillProfiles) {
        this.refillProfiles = refillProfiles;
    }

    @Override
    public PlayerHandler resolvePlayer(GameProfile profile) {
        return new PlayerHandler(this.refillProfiles.refill(profile));
    }

    public static class PlayerHandler extends AbstractPlayerHandler<MinecraftProfileTexture> {
        public PlayerHandler(@NotNull GameProfile profile) {
            this.textures.putAll(PlayerRegistry
                    .getSessionService()
                    .getTextures(profile, true));
            if (this.textures.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                this.setModel(textures
                        .get(MinecraftProfileTexture.Type.SKIN)
                        .getMetadata("model"));
            }
        }

        @Override
        public Identifier downloadTexture(MinecraftProfileTexture.Type type) {
            final MinecraftProfileTexture texture = this.textures.get(type);
            return MinecraftClient
                    .getInstance()
                    .getSkinProvider()
                    .loadSkin(texture, type);
        }
    }
}
