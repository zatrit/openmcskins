package net.zatrit.openmcskins.config;

import net.zatrit.openmcskins.resolvers.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

public class ConfigRepresenter extends Representer {
    public ConfigRepresenter() {
        this.representers.put(MojangAuthlibResolver.class, new MojangAuthlibResolverRepresent());
        this.representers.put(SimpleServerResolver.class, new SimpleServerResolverRepresent());
        this.representers.put(ElyByServerResolver.class, new ElyByServerResolverRepresent());
        this.representers.put(LocalDirectoryResolver.class, new LocalDirectoryResolverRepresent());
    }

    private static class MojangAuthlibResolverRepresent implements Represent {
        @Contract("_ -> new")
        @Override
        public @NotNull Node representData(Object data) {
            return new ScalarNode(new Tag("!mojang"), String.valueOf(((MojangAuthlibResolver) data).secureMode), null, null, DumperOptions.ScalarStyle.PLAIN);
        }
    }

    private static class SimpleServerResolverRepresent implements Represent {
        @Contract("_ -> new")
        @Override
        public @NotNull Node representData(Object data) {
            return new ScalarNode(new Tag("!server"), ((SimpleServerResolver) data).getHost(), null, null, DumperOptions.ScalarStyle.PLAIN);
        }
    }

    private static class LocalDirectoryResolverRepresent implements Represent {
        @Contract("_ -> new")
        @Override
        public @NotNull Node representData(Object data) {
            return new ScalarNode(new Tag("!local"), ((net.zatrit.openmcskins.resolvers.LocalDirectoryResolver) data).getDirectory(), null, null, DumperOptions.ScalarStyle.PLAIN);
        }
    }

    private static class ElyByServerResolverRepresent implements Represent {
        @Contract("_ -> new")
        @Override
        public @NotNull Node representData(Object data) {
            return new ScalarNode(new Tag("!elyby"), "", null, null, DumperOptions.ScalarStyle.PLAIN);
        }
    }
}