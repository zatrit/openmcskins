package net.zatrit.openmcskins.config;

import net.zatrit.openmcskins.HostType;
import net.zatrit.openmcskins.annotation.KeepClassMember;
import net.zatrit.openmcskins.resolvers.Resolver;

import javax.annotation.Nullable;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class HostConfigItem {
    @KeepClassMember
    public HostType type;
    @KeepClassMember
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
