package net.zatrit.openmcskins.util;

import joptsimple.internal.Strings;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.DumperOptions;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.Yaml;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.constructor.AbstractConstruct;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.constructor.Constructor;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.nodes.Node;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.nodes.ScalarNode;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.nodes.Tag;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.representer.Represent;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.representer.Representer;
import net.zatrit.openmcskins.HostType;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.config.HostConfigItem;
import net.zatrit.openmcskins.config.OpenMCSkinsConfig;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.MoreObjects.firstNonNull;

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

    public static class ConfigConstructor extends Constructor {
        public ConfigConstructor() {
            super(OpenMCSkinsConfig.class);

            for (HostType type : HostType.values())
                this.yamlConstructors.put(type.getTag(), new HostConstruct(type));
        }

        public class HostConstruct extends AbstractConstruct {
            private final HostType type;

            public HostConstruct(HostType type) {
                this.type = type;
            }

            @Override
            public @NotNull Object construct(Node node) {
                String nodeAsString = constructScalar((ScalarNode) node);
                return new HostConfigItem(type, nodeAsString);
            }
        }
    }

    public static class ConfigRepresenter extends Representer {
        public ConfigRepresenter() {
            this.representers.put(HostConfigItem.class, new HostConfigItemRepresent());
            this.addClassTag(OpenMCSkinsConfig.class, Tag.MAP);
        }

        private static class HostConfigItemRepresent implements Represent {
            @Contract("_ -> new")
            @Override
            public @NotNull Node representData(Object data) {
                HostConfigItem item = (HostConfigItem) data;
                return new ScalarNode(item.type.getTag(), firstNonNull(item.value, ""), null, null, DumperOptions.ScalarStyle.PLAIN);
            }
        }
    }
}
