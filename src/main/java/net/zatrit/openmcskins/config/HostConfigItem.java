package net.zatrit.openmcskins.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler;
import net.zatrit.openmcskins.Hosts;
import net.zatrit.openmcskins.annotation.KeepClassMember;
import net.zatrit.openmcskins.resolvers.Resolver;

import javax.annotation.Nullable;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class HostConfigItem {
    @KeepClassMember
    public String data;
    @KeepClassMember
    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    public Hosts type;

    public HostConfigItem(Hosts type, @Nullable String data) {
        this.type = type;
        this.data = data;
    }

    public String getData() {
        return firstNonNull(data, "");
    }

    public Resolver<?> createResolver() {
        return this.type.createResolver(data);
    }
}
