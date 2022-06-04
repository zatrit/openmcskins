package net.zatrit.openmcskins.config.options;

import net.zatrit.openmcskins.api.resolver.Resolver;

import javax.annotation.Nullable;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class ConfigHostOption {
    public HostType type;
    private final String value;

    public ConfigHostOption(HostType type, @Nullable String value) {
        this.type = type;
        this.value = value;
    }

    public String getValue() {
        return firstNonNull(value, "");
    }

    public Resolver<?> createResolver() {
        return this.type.createResolver(getValue());
    }
}
