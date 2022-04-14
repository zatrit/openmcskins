package net.zatrit.openmcskins.resolvers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.resources.ResourceLocation;
import net.zatrit.openmcskins.enums.SecureMode;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class MojangAuthlibResolver extends AbstractResolver<MojangAuthlibResolver.PlayerData> {
    public SecureMode secureMode;

    @Override
    public PlayerData resolvePlayer(@NotNull PlayerInfo playerInfo) {
        return new PlayerData(playerInfo.getProfile());
    }

    public MojangAuthlibResolver(SecureMode secure) {
        this.secureMode = secure;
    }

    @Override
    public String getName() {
        return "MojangAuthlib";
    }

    public class PlayerData extends AbstractResolver.PlayerData {
        private final static MinecraftSessionService SESSION_SERVICE = Minecraft.getInstance().getMinecraftSessionService();
        private final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures;

        public PlayerData(@NotNull GameProfile profile) {
            boolean secure = secureMode == SecureMode.SECURE;
            if (profile.getProperties().isEmpty()) SESSION_SERVICE.fillProfileProperties(profile, secure);
            this.textures = SESSION_SERVICE.getTextures(profile, secure);
            if (textures.containsKey(MinecraftProfileTexture.Type.SKIN))
                model = textures.get(MinecraftProfileTexture.Type.SKIN).getMetadata("model");
            if(model == null)
                model = "default";
        }

        @Override
        public ResourceLocation downloadTexture(MinecraftProfileTexture.Type type) {
            MinecraftProfileTexture texture = this.textures.get(type);
            return Minecraft.getInstance().getSkinManager().registerTexture(texture, type);
        }

        @Override
        public boolean hasTexture(MinecraftProfileTexture.Type type) {
            return this.textures.containsKey(type);
        }
    }
}
