package net.zatrit.openmcskins.util;

import net.zatrit.openmcskins.OpenMCSkinsConfig;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.*;

public class ConfigFile implements Serializable {
    private static final Yaml YAML = new Yaml(new ConfigConstructor(), new ConfigRepresenter());

    public static OpenMCSkinsConfig load(@NotNull File file) throws IOException {
        if (!file.isFile()) {
            OpenMCSkinsConfig configFile = new OpenMCSkinsConfig();

            file.getParentFile().mkdirs();
            FileWriter configFileWriter = new FileWriter(file);
            configFileWriter.write(YAML.dumpAs(configFile, Tag.MAP, DumperOptions.FlowStyle.BLOCK));
            configFileWriter.close();

            return configFile;
        }

        return YAML.load(new FileReader(file));
    }
}
