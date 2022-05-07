package net.zatrit.openmcskins.config;

import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.util.Utils;
import net.zatrit.openmcskins.Hosts;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.util.CollectionUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public record SnakeYamlSerializer(Config definition,
                                  Class<OpenMCSkinsConfig> configClass) implements ConfigSerializer<OpenMCSkinsConfig> {

    private static final Yaml YAML = new Yaml(new ConfigConstructor(), new ConfigRepresenter());

    public static List<String> getHostsAsStrings(@NotNull OpenMCSkinsConfig config) {
        return config.hosts.stream().map(data -> YAML.dump(data).strip().replaceFirst("!", "").replace(" ''", "")).toList();
    }

    public static List<HostConfigItem> getHostsFromStrings(@NotNull List<String> strings) {
        return strings.stream().map(x -> {
            try {
                String[] split = x.split(" ");
                String data = CollectionUtils.getOrDefault(split, 1, "").replace("'", "");
                Hosts type = Hosts.valueOf(split[0].toUpperCase());

                return new HostConfigItem(type, data);
            } catch (Exception ex) {
                OpenMCSkins.handleError(ex);
                return null;
            }
        }).filter(Objects::nonNull).toList();
    }

    private @NotNull Path getConfigPath() {
        return Utils.getConfigFolder().resolve(this.definition.name() + ".yml");
    }

    @Override
    public void serialize(OpenMCSkinsConfig t) throws SerializationException {
        try {
            try (FileWriter configFileWriter = new FileWriter(getConfigPath().toFile())) {
                configFileWriter.write(YAML.dumpAs(t, Tag.MAP, DumperOptions.FlowStyle.BLOCK));
            }
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    @Override
    public OpenMCSkinsConfig deserialize() throws SerializationException {
        try {
            return YAML.load(new FileReader(getConfigPath().toFile()));
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }

    @Contract(" -> new")
    @Override
    public @NotNull OpenMCSkinsConfig createDefault() {
        return new OpenMCSkinsConfig();
    }

    public static class ConfigConstructor extends Constructor {
        public ConfigConstructor() {
            super(OpenMCSkinsConfig.class);

            for (Hosts type : Hosts.values())
                this.yamlConstructors.put(type.getTag(), new HostConstruct(type));
        }

        public class HostConstruct extends AbstractConstruct {
            private final Hosts type;

            public HostConstruct(Hosts type) {
                this.type = type;
            }

            @Override
            public @NotNull Object construct(Node node) {
                String nodeAsString = constructScalar((ScalarNode) node);
                return type.createHostConfigItem(nodeAsString);
            }
        }
    }

    public static class ConfigRepresenter extends Representer {
        public ConfigRepresenter() {
            this.representers.put(HostConfigItem.class, new HostConfigItemRepresent());
        }

        private static class HostConfigItemRepresent implements Represent {
            @Contract("_ -> new")
            @Override
            public @NotNull Node representData(Object data) {
                HostConfigItem item = (HostConfigItem) data;
                return new ScalarNode(item.type.getTag(), item.getData(), null, null, DumperOptions.ScalarStyle.PLAIN);
            }
        }
    }
}
