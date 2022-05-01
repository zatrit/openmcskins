package net.zatrit.openmcskins.util;

import com.mojang.authlib.GameProfile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;

import static com.google.common.base.MoreObjects.firstNonNull;

public class ObjectUtils {
    @Contract(pure = true)
    public static <T> @Nullable T getOrNull(T @NotNull [] array, int index) {
        if (index >= array.length)
            return null;
        return array[index];
    }

    public static <T extends Enum<T>> @NotNull T valueOfOrDefault(Class<T> clazz, String str, T d) {
        try {
            return T.valueOf(clazz, str);
        } catch (Exception ignore) {
            return d;
        }
    }

    public static <T> @NotNull Object getOfDefaultNonGeneric(@NotNull Map<T, ?> map, T key, Object d) {
        return firstNonNull(map.get(key), d);
    }

    public static @NotNull GameProfile setGameProfileUUID(@NotNull GameProfile profile, UUID uuid) {
        GameProfile gameProfile = new GameProfile(uuid, profile.getName());
        gameProfile.getProperties().putAll(profile.getProperties());
        return gameProfile;
    }
}
