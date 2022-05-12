package net.zatrit.openmcskins.util;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.google.common.base.MoreObjects.firstNonNull;

public final class CollectionUtils {

    private CollectionUtils() {
    }

    public static <T> @NotNull Object getOrDefaultNonGeneric(@NotNull Map<T, ?> map, T key, Object d) {
        return firstNonNull(map.get(key), d);
    }
}
