package net.zatrit.openmcskins.config.yaml;

import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.constructor.AbstractConstruct;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.constructor.Constructor;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.nodes.Node;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.nodes.ScalarNode;
import net.zatrit.openmcskins.config.Config;
import net.zatrit.openmcskins.operators.ConfigHostOption;
import net.zatrit.openmcskins.operators.Host;
import org.jetbrains.annotations.NotNull;

public class ConfigConstructor extends Constructor {
    public ConfigConstructor() {
        super(Config.class);

        for (Host type : Host.values())
            this.yamlConstructors.put(type.getTag(), new HostConstructRepresent(type));
    }

    private class HostConstructRepresent extends AbstractConstruct {
        private final Host type;

        public HostConstructRepresent(Host type) {
            this.type = type;
        }

        @Override
        public @NotNull Object construct(Node node) {
            final String nodeAsString = ConfigConstructor.this.constructScalar((ScalarNode) node);
            return new ConfigHostOption(type, nodeAsString);
        }
    }
}

