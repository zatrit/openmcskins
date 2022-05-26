package net.zatrit.openmcskins.util;

import joptsimple.internal.Strings;
import net.zatrit.openmcskins.HostType;
import net.zatrit.openmcskins.mod.config.HostConfigItem;
import net.zatrit.openmcskins.mod.config.OpenMCSkinsConfig;
import net.zatrit.openmcskins.mod.OpenMCSkins;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class ConfigUtil {
    private ConfigUtil() {
    }

    public static List<String> getHostsAsStrings(@NotNull OpenMCSkinsConfig config) {
        return config.hosts.stream().parallel().map(x -> {
            String type = x.type.toString().toLowerCase();
            return Strings.isNullOrEmpty(x.value) ? type : type + ": " + x.value;
        }).toList();
    }

    public static List<HostConfigItem> getHostsFromStrings(@NotNull List<String> strings) {
        return strings.stream().parallel().map(x -> {
            try {
                String[] split = Arrays.stream(x.split(":")).parallel().map(String::trim).toArray(String[]::new);
                String value = null;
                if (split.length > 1) value = String.join(":", Arrays.copyOfRange(split, 1, split.length));
                return new HostConfigItem(HostType.valueOf(split[0].toUpperCase()), value);
            } catch (IllegalArgumentException e) {
                OpenMCSkins.handleError(e);
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }
}
