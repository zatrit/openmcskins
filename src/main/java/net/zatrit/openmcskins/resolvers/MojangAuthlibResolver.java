package net.zatrit.openmcskins.resolvers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.config.SecureMode;
import org.jetbrains.annotations.NotNull;

public class MojangAuthlibResolver extends AbstractResolver<MojangAuthlibResolver.IndexedPlayerData> {
    public final SecureMode secureMode;

    public MojangAuthlibResolver(SecureMode secure) {
        this.secureMode = secure;
    }

    @Override
    public IndexedPlayerData resolvePlayer(@NotNull PlayerListEntry player) {
        return new IndexedPlayerData(player.getProfile());
    }

    public class IndexedPlayerData extends AbstractResolver.IndexedPlayerData<MinecraftProfileTexture> {
        private final static MinecraftSessionService SESSION_SERVICE = MinecraftClient.getInstance().getSessionService();

        public IndexedPlayerData(@NotNull GameProfile profile) {
            boolean secure = secureMode == SecureMode.SECURE;
            if (profile.getProperties().isEmpty()) SESSION_SERVICE.fillProfileProperties(profile, secure);
            this.textures.putAll(SESSION_SERVICE.getTextures(profile, secure));
            if (this.textures.containsKey(MinecraftProfileTexture.Type.SKIN))
                this.model = textures.get(MinecraftProfileTexture.Type.SKIN).getMetadata("model");
            if (this.model == null) this.model = "default";
        }

        @Override
        public Identifier downloadTexture(MinecraftProfileTexture.Type type) {
            MinecraftProfileTexture texture = this.textures.get(type);
            return MinecraftClient.getInstance().getSkinProvider().loadSkin(texture, type);
        }
    }
}
