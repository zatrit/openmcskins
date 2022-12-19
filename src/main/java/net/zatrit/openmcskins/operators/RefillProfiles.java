package net.zatrit.openmcskins.operators;

import com.mojang.authlib.GameProfile;
import net.zatrit.openmcskins.skins.PlayerRegistry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum RefillProfiles {
    REFILL_ALWAYS,
    REFILL_EMPTY,
    DONT_REFILL;

    @Contract("_ -> param1")
    public @NotNull GameProfile refill(GameProfile profile) {
        if (this == RefillProfiles.REFILL_ALWAYS) {
            profile.getProperties().clear();
        }
        if (profile.getProperties().isEmpty() &&
                this != RefillProfiles.DONT_REFILL) {
            PlayerRegistry
                    .getSessionService()
                    .fillProfileProperties(profile, true);
        }
        return profile;
    }
}
