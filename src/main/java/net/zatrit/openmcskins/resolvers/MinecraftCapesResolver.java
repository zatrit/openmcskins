package net.zatrit.openmcskins.resolvers;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class MinecraftCapesResolver extends AbstractResolver<MinecraftCapesResolver.AnimatedPlayerData> {
    @Override
    public AnimatedPlayerData resolvePlayer(GameProfile profile) {
        return new AnimatedPlayerData(profile);
    }

    public static class AnimatedPlayerData extends IndexedPlayerData<Object> {
        public AnimatedPlayerData(GameProfile profile) {

        }

        @Override
        public @Nullable Identifier downloadTexture(MinecraftProfileTexture.Type type) {
            return null;
        }
    }
}
