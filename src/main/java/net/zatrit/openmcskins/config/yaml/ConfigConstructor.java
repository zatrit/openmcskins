package net.zatrit.openmcskins.config.yaml;

import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.constructor.AbstractConstruct;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.constructor.Constructor;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.nodes.Node;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.nodes.ScalarNode;
import net.zatrit.openmcskins.config.options.HostType;
import net.zatrit.openmcskins.config.options.ConfigHostOption;
import net.zatrit.openmcskins.config.Config;
import org.jetbrains.annotations.NotNull;

public class ConfigConstructor extends Constructor {
    public ConfigConstructor() {
        super(Config.class);

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
            String nodeAsString = ConfigConstructor.this.constructScalar((ScalarNode) node);
            return new ConfigHostOption(type, nodeAsString);
        }
    }
}

