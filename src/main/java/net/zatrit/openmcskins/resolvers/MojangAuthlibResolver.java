package net.zatrit.openmcskins.resolvers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.yggdrasil.YggdrasilGameProfileRepository;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.config.AuthlibResolverMode;
import net.zatrit.openmcskins.util.NetworkUtils;
import net.zatrit.openmcskins.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MojangAuthlibResolver extends AbstractResolver<MojangAuthlibResolver.PlayerData> {
    private final AuthlibResolverMode mode;

    public MojangAuthlibResolver(AuthlibResolverMode mode) {
        this.mode = mode;
    }

    @Override
    public PlayerData resolvePlayer(@NotNull PlayerListEntry player) {
        return new PlayerData(player.getProfile());
    }

    public class PlayerData extends AbstractResolver.IndexedPlayerData<MinecraftProfileTexture> {
        private final static YggdrasilMinecraftSessionService SESSION_SERVICE = (YggdrasilMinecraftSessionService) MinecraftClient.getInstance().getSessionService();
        private final static YggdrasilGameProfileRepository PROFILE_REPOSITORY = (YggdrasilGameProfileRepository) SESSION_SERVICE.getAuthenticationService().createProfileRepository();

        public PlayerData(@NotNull GameProfile profile) {
            if (profile.getName() != null && mode == AuthlibResolverMode.OFFLINE) {
                final UUID id = NetworkUtils.getUUIDByName(PROFILE_REPOSITORY, profile.getName());
                profile = ObjectUtils.setGameProfileUUID(profile, id);
            }

            if (profile.getProperties().isEmpty()) SESSION_SERVICE.fillProfileProperties(profile, true);
            PlayerData.this.textures.putAll(SESSION_SERVICE.getTextures(profile, true));
            if (PlayerData.this.textures.containsKey(MinecraftProfileTexture.Type.SKIN))
                PlayerData.this.model = textures.get(MinecraftProfileTexture.Type.SKIN).getMetadata("model");
            if (PlayerData.this.model == null) PlayerData.this.model = "default";
        }

        @Override
        public Identifier downloadTexture(MinecraftProfileTexture.Type type) {
            MinecraftProfileTexture texture = this.textures.get(type);
            return MinecraftClient.getInstance().getSkinProvider().loadSkin(texture, type);
        }
    }
}
