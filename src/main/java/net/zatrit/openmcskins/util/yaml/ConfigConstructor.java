package net.zatrit.openmcskins.util.yaml;

import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.constructor.AbstractConstruct;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.constructor.Constructor;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.nodes.Node;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.nodes.ScalarNode;
import net.zatrit.openmcskins.HostType;
import net.zatrit.openmcskins.config.HostConfigItem;
import net.zatrit.openmcskins.config.OpenMCSkinsConfig;
import org.jetbrains.annotations.NotNull;

public class ConfigConstructor extends Constructor {
    public ConfigConstructor() {
        super(OpenMCSkinsConfig.class);

        for (HostType type : HostType.values())
            this.yamlConstructors.put(type.getTag(), new HostConstructRepresent(type));
    }

    public class HostConstructRepresent extends AbstractConstruct {
        private final HostType type;

        public HostConstructRepresent(HostType type) {
            this.type = type;
        }

        @Override
        public @NotNull Object construct(Node node) {
            String nodeAsString = constructScalar((ScalarNode) node);
            return new HostConfigItem(type, nodeAsString);
        }
    }
}

