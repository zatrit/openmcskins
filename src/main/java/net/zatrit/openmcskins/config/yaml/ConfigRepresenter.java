package net.zatrit.openmcskins.config.yaml;

import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.DumperOptions;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.nodes.Node;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.nodes.ScalarNode;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.nodes.Tag;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.representer.Represent;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.representer.Representer;
import net.zatrit.openmcskins.config.Config;
import net.zatrit.openmcskins.operators.ConfigHostOption;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ConfigRepresenter extends Representer {
    public ConfigRepresenter() {
        this.representers.put(ConfigHostOption.class, new HostConfigItemRepresent());
        this.addClassTag(Config.class, Tag.MAP);
    }

    private static class HostConfigItemRepresent implements Represent {
        @Contract("_ -> new")
        @Override
        public @NotNull Node representData(Object data) {
            final ConfigHostOption item = (ConfigHostOption) data;
            return new ScalarNode(item.type.getTag(), item.getValue(), null, null, DumperOptions.ScalarStyle.PLAIN);
        }
    }
}
