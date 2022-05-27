package net.zatrit.openmcskins.mod.config;

import net.zatrit.openmcskins.HostType;
import net.zatrit.openmcskins.api.resolver.Resolver;

import javax.annotation.Nullable;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class HostConfigItem {
    public HostType type;
    public String value;

    public HostConfigItem(HostType type, @Nullable String value) {
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
