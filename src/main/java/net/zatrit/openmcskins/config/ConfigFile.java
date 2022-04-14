package net.zatrit.openmcskins.config;

import net.zatrit.openmcskins.annotation.DontObfuscate;
import net.zatrit.openmcskins.enums.SecureMode;
import net.zatrit.openmcskins.resolvers.AbstractResolver;
import net.zatrit.openmcskins.resolvers.LocalDirectoryResolver;
import net.zatrit.openmcskins.resolvers.MojangAuthlibResolver;
import net.zatrit.openmcskins.resolvers.SimpleServerResolver;
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
import java.util.ArrayList;
import java.util.List;

public class ConfigFile implements Serializable {
    private static final Yaml YAML = new Yaml(new ConfigConstructor(), new ConfigRepresenter());

    @DontObfuscate
    public int resolvingTimeout = 5;
    @DontObfuscate
    public List<AbstractResolver<? extends AbstractResolver.PlayerData>> hosts = new ArrayList<>();

    public static ConfigFile load(@NotNull File file) throws IOException {
        if (!file.isFile()) {
            ConfigFile configFile = new ConfigFile();

            file.getParentFile().mkdirs();
            FileWriter configFileWriter = new FileWriter(file);
            configFileWriter.write(YAML.dumpAs(configFile, Tag.MAP, DumperOptions.FlowStyle.BLOCK));
            configFileWriter.close();

            return configFile;
        }

        return YAML.load(new FileReader(file));
    }

    public ConfigFile() {
        this.hosts.add(new MojangAuthlibResolver(SecureMode.SECURE));
    }
}
