package net.zatrit.openmcskins.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import static com.google.common.base.MoreObjects.firstNonNull;

public final class CollectionUtils {

    @Contract(pure = true)
    public static <T> @NotNull T getOrDefault(T @NotNull [] array, int index, T d) {
        if (index >= array.length)
            return d;
        return array[index];
    }

    public static <T> @NotNull Object getOfDefaultNonGeneric(@NotNull Map<T, ?> map, T key, Object d) {
        return firstNonNull(map.get(key), d);
    }

    private CollectionUtils() {
    }
}
