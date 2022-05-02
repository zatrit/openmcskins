package net.zatrit.openmcskins.config;

import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.EnumHandler;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.zatrit.openmcskins.annotation.KeepClassMember;
import net.zatrit.openmcskins.resolvers.*;
import net.zatrit.openmcskins.util.ObjectUtils;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Objects;

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

    public static HostConfigItem fromTypeAndString(HostType type, String data) {
        String dataOrEmptyString = firstNonNull(data, "").replace("'", "");
        return switch (type) {
            case SERVER, LOCAL, MOJANG -> new HostConfigItem(type, dataOrEmptyString);
            default -> new HostConfigItem(type, null);
        };
    }

    public String getData() {
        return firstNonNull(data, "");
    }

    public AbstractResolver<?> createResolver() {
        return switch (this.type) {
            case MOJANG ->
                    new MojangAuthlibResolver(ObjectUtils.valueOfOrDefault(AuthlibResolverMode.class, getData(), AuthlibResolverMode.ONLINE));
            case OPTIFINE -> new OptifineCapeResolver();
            case LOCAL -> new LocalDirectoryResolver(new File(Objects.requireNonNull(data)));
            case SERVER -> new SimpleServerResolver(data);
            case ELYBY -> new ElyByServerResolver();
        };
    }

    public Text getText() {
        return switch (type) {
            case MOJANG -> new TranslatableText("text.openmcskins.authlib");
            case OPTIFINE -> new TranslatableText("text.openmcskins.optifine");
            case LOCAL, SERVER -> Text.of(getData());
            case ELYBY -> new TranslatableText("text.openmcskins.elyby");
        };
    }
}
