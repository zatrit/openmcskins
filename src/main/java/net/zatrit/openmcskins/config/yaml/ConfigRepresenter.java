package net.zatrit.openmcskins.config.yaml;

import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.DumperOptions;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.nodes.Node;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.nodes.ScalarNode;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.nodes.Tag;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.representer.Represent;
import me.shedaniel.cloth.clothconfig.shadowed.org.yaml.snakeyaml.representer.Representer;
import net.zatrit.openmcskins.config.options.ConfigHostOption;
import net.zatrit.openmcskins.config.Config;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static com.google.common.base.MoreObjects.firstNonNull;

public class ConfigRepresenter extends Representer {
    public ConfigRepresenter() {
        this.representers.put(ConfigHostOption.class, new HostConfigItemRepresent());
        this.addClassTag(Config.class, Tag.MAP);
    }

    private static class HostConfigItemRepresent implements Represent {
        @Contract("_ -> new")
        @Override
        public @NotNull Node representData(Object data) {
            ConfigHostOption item = (ConfigHostOption) data;
            return new ScalarNode(item.type.getTag(), firstNonNull(item.value, ""), null, null, DumperOptions.ScalarStyle.PLAIN);
        }
    }
}
