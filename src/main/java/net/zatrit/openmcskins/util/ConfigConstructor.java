package net.zatrit.openmcskins.util;

import net.zatrit.openmcskins.OpenMCSkinsConfig;
import net.zatrit.openmcskins.enums.SecureMode;
import net.zatrit.openmcskins.resolvers.ElyByServerResolver;
import net.zatrit.openmcskins.resolvers.LocalDirectoryResolver;
import net.zatrit.openmcskins.resolvers.MojangAuthlibResolver;
import net.zatrit.openmcskins.resolvers.SimpleServerResolver;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.File;

public class ConfigConstructor extends Constructor {
    public ConfigConstructor() {
        super(OpenMCSkinsConfig.class);

        this.yamlConstructors.put(new Tag("!server"), new SimpleServerResolverConstruct());
        this.yamlConstructors.put(new Tag("!mojang"), new MojangAuthlibResolverConstruct());
        this.yamlConstructors.put(new Tag("!elyby"), new ElyByResolverConstruct());
        this.yamlConstructors.put(new Tag("!local"), new LocalDirectoryResolverConstruct());
    }

    private static class ElyByResolverConstruct extends AbstractConstruct {
        @Override
        public @NotNull Object construct(Node node) {
            return new ElyByServerResolver();
        }
    }

    private class MojangAuthlibResolverConstruct extends AbstractConstruct {
        @Override
        public @NotNull Object construct(Node node) {
            SecureMode secure = SecureMode.valueOf(constructScalar((ScalarNode) node));
            return new MojangAuthlibResolver(secure);
        }
    }

    private class SimpleServerResolverConstruct extends AbstractConstruct {
        @Override
        public @NotNull Object construct(Node node) {
            String host = constructScalar((ScalarNode) node);
            return new SimpleServerResolver(host);
        }
    }

    private class LocalDirectoryResolverConstruct extends AbstractConstruct {
        @Override
        public @NotNull Object construct(Node node) {
            String directory = constructScalar((ScalarNode) node);
            return new LocalDirectoryResolver(new File(directory));
        }
    }
}