package net.zatrit.openmcskins.config;

import joptsimple.internal.Strings;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.operators.ConfigHostOption;
import net.zatrit.openmcskins.operators.Host;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class ConfigUtil {
    private ConfigUtil() {
    }

    public static List<String> getHostsAsStrings(@NotNull Config config) {
        return config.hosts.stream().parallel().map(x -> {
            final String type = x.type.toString().toLowerCase();
            final String value = x.getValue();
            return Strings.isNullOrEmpty(value) ? type : type + ": " + value;
        }).toList();
    }

    public static List<ConfigHostOption> getHostsFromStrings(@NotNull List<String> strings) {
        return strings.stream().parallel().map(x -> {
            try {
                final String[] split = Arrays.stream(x.split(":")).parallel().map(String::trim).toArray(String[]::new);
                String value = null;
                if (split.length > 1) {
                    value = String.join(":", Arrays.copyOfRange(split, 1, split.length));
                }
                return new ConfigHostOption(Host.valueOf(split[0].toUpperCase()), value);
            } catch (IllegalArgumentException e) {
                OpenMCSkins.handleError(e);
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }
}
