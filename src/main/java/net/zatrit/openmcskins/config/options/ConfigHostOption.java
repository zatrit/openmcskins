package net.zatrit.openmcskins.config.options;

import net.zatrit.openmcskins.api.resolver.Resolver;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Nullable;

public class ConfigHostOption {
    private final String value;
    public HostType type;

    public ConfigHostOption(HostType type, @Nullable String value) {
        this.type = type;
        this.value = value;
    }

    public String getValue() {
        return ObjectUtils.firstNonNull(value, "");
    }

    public Resolver<?> createResolver() {
        return this.type.createResolver(getValue());
    }
}
