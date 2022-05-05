package net.zatrit.openmcskins.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler;
import net.zatrit.openmcskins.annotation.KeepClassMember;
import net.zatrit.openmcskins.resolvers.AbstractResolver;
import net.zatrit.openmcskins.HostType;
import org.jetbrains.annotations.ApiStatus;

import javax.annotation.Nullable;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

public class HostConfigItem {
    @KeepClassMember
    public String data;
    @KeepClassMember
    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    public HostType type;

    public HostConfigItem(HostType type, @Nullable String data) {
        this.type = type;
        this.data = data;
    }

    public String getData() {
        return firstNonNull(data, "");
    }

    public AbstractResolver<?> createResolver() {
        return this.type.createResolver(data);
    }
}
