package net.zatrit.openmcskins.config;

import net.zatrit.openmcskins.resolvers.*;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Objects;

import static net.zatrit.openmcskins.util.ObjectUtils.valueOfOrDefault;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class HostConfigItem<T> {
    private final T data;
    public HostType type;

    public HostConfigItem(HostType type, @Nullable T data) {
        this.type = type;
        this.data = data;
    }

    public static HostConfigItem<?> fromTypeAndString(HostType type, String data) {
        String dataOrEmptyString = firstNonNull(data, "").replace("'", "");
        return switch (type) {
            case MOJANG ->
                    new HostConfigItem<>(type, valueOfOrDefault(SecureMode.class, dataOrEmptyString.toUpperCase(), SecureMode.SECURE));
            case SERVER, LOCAL -> new HostConfigItem<>(type, dataOrEmptyString);
            default -> new HostConfigItem<>(type, null);
        };
    }

    public String getData() {
        return firstNonNull(data, "").toString();
    }

    public AbstractResolver<?> createResolver() {
        return switch (this.type) {
            case MOJANG -> new MojangAuthlibResolver((SecureMode) data);
            case OPTIFINE -> new OptifineCapeResolver();
            case LOCAL -> new LocalDirectoryResolver(new File((String) Objects.requireNonNull(data)));
            case SERVER -> new SimpleServerResolver((String) data);
            case ELYBY -> new ElyByServerResolver();
        };
    }
}
