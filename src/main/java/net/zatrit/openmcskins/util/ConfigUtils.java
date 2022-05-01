package net.zatrit.openmcskins.util;

import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.config.HostConfigItem;
import net.zatrit.openmcskins.config.HostType;
import net.zatrit.openmcskins.config.OpenMCSkinsConfig;
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

import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.zatrit.openmcskins.util.ObjectUtils.getOrNull;

public class ConfigUtils implements Serializable {
    private static final Yaml YAML = new Yaml(new ConfigConstructor(), new ConfigRepresenter());

    public static OpenMCSkinsConfig load(@NotNull File file) throws IOException {
        if (!file.isFile()) {
            OpenMCSkinsConfig configFile = new OpenMCSkinsConfig();
            save(configFile, file);

            return configFile;
        }

        return YAML.load(new FileReader(file));
    }

    public static void save(OpenMCSkinsConfig config, @NotNull File file) throws IOException {
        boolean directorySuccessfullyCreated = file.getParentFile().mkdirs();
        if (directorySuccessfullyCreated)
            OpenMCSkins.LOGGER.info("Config directory created");
        FileWriter configFileWriter = new FileWriter(file);
        configFileWriter.write(YAML.dumpAs(config, Tag.MAP, DumperOptions.FlowStyle.BLOCK));
        configFileWriter.close();
    }

    public static List<String> hostsToStrings(@NotNull OpenMCSkinsConfig config) {
        return config.getHosts().stream().map(host -> YAML.dump(host).trim().replaceFirst("!", "").replace(" ''", "")).toList();
    }

    // TODO: REWRITE
    public static List<HostConfigItem<?>> stringsToHost(@NotNull List<String> hostStrings) {
        return hostStrings.stream().map(hostName -> {
            try {
                String[] splitHostName = hostName.split(" ");
                HostType hostType = HostType.valueOf(splitHostName[0].toUpperCase());
                return HostConfigItem.fromTypeAndString(hostType, getOrNull(splitHostName, 1));
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static class ConfigRepresenter extends Representer {
        public ConfigRepresenter() {
            this.representers.put(HostConfigItem.class, new HostConfigItemRepresent());
        }

        private static class HostConfigItemRepresent implements Represent {
            @Contract("_ -> new")
            @Override
            public @NotNull Node representData(Object data) {
                HostConfigItem<?> item = (HostConfigItem<?>) data;
                return new ScalarNode(item.type.getTag(), item.getData(), null, null, DumperOptions.ScalarStyle.PLAIN);
            }
        }
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
                return HostConfigItem.fromTypeAndString(type, nodeAsString);
            }
        }
    }
}
