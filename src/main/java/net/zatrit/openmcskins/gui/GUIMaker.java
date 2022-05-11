package net.zatrit.openmcskins.gui;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.ModMenuEntry;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.config.OpenMCSkinsConfig;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class GUIMaker {
    private static final Identifier REFRESH_BUTTON_LOCATION = new Identifier(OpenMCSkins.MOD_ID, "textures/gui/refresh_button.png");
    private static final List<Text> REFRESH_CONFIG_BUTTON_TOOLTIP = textListFromKey("openmcskins.reloadConfig");
    private static final Identifier CONFIGURE_BUTTON_LOCATION = new Identifier(OpenMCSkins.MOD_ID, "textures/gui/configure_button.png");
    private static final List<Text> CONFIGURE_BUTTON_TOOLTIP = textListFromKey("openmcskins.configure");

    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull ButtonWidget createRefreshConfigButton(int x, int y, Screen screen) {
        ButtonWidget.PressAction buttonClick = b -> OpenMCSkins.reloadConfig();
        return createButton(x, y, screen, REFRESH_CONFIG_BUTTON_TOOLTIP, REFRESH_BUTTON_LOCATION, buttonClick);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull ButtonWidget createConfigureButton(int x, int y, Screen screen) {
        ButtonWidget.PressAction buttonClick = b -> MinecraftClient.getInstance().setScreen(AutoConfig.getConfigScreen(OpenMCSkinsConfig.class, screen).get());
        return createButton(x, y, screen, CONFIGURE_BUTTON_TOOLTIP, CONFIGURE_BUTTON_LOCATION, buttonClick);
    }

    private static @NotNull ButtonWidget createButton(int x, int y, Screen screen, List<Text> tooltip, Identifier resource, ButtonWidget.PressAction buttonClick) {
        ButtonWidget.TooltipSupplier renderTooltip = (ButtonWidget b, MatrixStack ma, int mx, int my) -> screen.renderTooltip(ma, tooltip, mx, my);
        return new TexturedButtonWidget(x, y, 20, 20, 0, 0, 20, resource, 20, 40, buttonClick, renderTooltip, null);
    }

    public static List<Text> textListFromKey(String key) {
        return Arrays.stream(I18n.translate(key).split("\n")).parallel().map(x -> (Text) new LiteralText(x)).toList();
    }
}
