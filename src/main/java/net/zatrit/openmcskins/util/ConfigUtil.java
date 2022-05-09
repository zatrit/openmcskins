package net.zatrit.openmcskins.util;

import net.zatrit.openmcskins.Hosts;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.config.HostConfigItem;
import net.zatrit.openmcskins.config.OpenMCSkinsConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class ConfigUtil {
    public static List<String> getHostsAsStrings(@NotNull OpenMCSkinsConfig config) {
        return config.hosts.stream().map(x -> {
            String type = x.type.toString().toLowerCase();
            String[] values = x.value == null ? new String[]{type} : new String[]{type, x.value};
            return String.join(": ", values);
        }).toList();
    }

    public static List<HostConfigItem> getHostsFromStrings(@NotNull List<String> strings) {
        return strings.stream().map(x -> {
            try {
                String[] split = Arrays.stream(x.split(":")).map(String::trim).toArray(String[]::new);
                String value = null;
                if (split.length > 1) value = String.join(":", Arrays.copyOfRange(split, 1, split.length));
                return new HostConfigItem(Hosts.valueOf(split[0].toUpperCase()), value);
            } catch (IllegalArgumentException e) {
                OpenMCSkins.handleError(e);
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }

    private ConfigUtil() {
    }
}
