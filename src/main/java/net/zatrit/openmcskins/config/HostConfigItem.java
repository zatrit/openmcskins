package net.zatrit.openmcskins.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler;
import net.zatrit.openmcskins.Hosts;
import net.zatrit.openmcskins.annotation.KeepClassMember;
import net.zatrit.openmcskins.resolvers.Resolver;

import javax.annotation.Nullable;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class HostConfigItem {
    @KeepClassMember
    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    public Hosts type;
    @KeepClassMember
    public String value;

    public HostConfigItem(Hosts type, @Nullable String value) {
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
