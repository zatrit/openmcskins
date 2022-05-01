package net.zatrit.openmcskins.gui;

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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class GUIMaker {
    private static final Identifier REFRESH_BUTTON_LOCATION = new Identifier(OpenMCSkins.MOD_ID, "textures/gui/refresh_button.png");
    private static final List<Text> RELOAD_CONFIG_TOOLTIP = textListFromKey("openmcskins.reloadConfig");
    private static final Identifier CONFIGURE_BUTTON_LOCATION = new Identifier(OpenMCSkins.MOD_ID, "textures/gui/configure_button.png");
    private static final List<Text> CONFIGURE_TOOLTIP = textListFromKey("openmcskins.configure");

    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull ButtonWidget refreshConfigButton(int x, int y, Screen screen) {
        ButtonWidget.TooltipSupplier renderTooltip = (ButtonWidget b, MatrixStack ma, int mx, int my) -> screen.renderTooltip(ma, RELOAD_CONFIG_TOOLTIP, mx, my);
        ButtonWidget.PressAction buttonClick = b -> OpenMCSkins.reloadConfig();

        return new TexturedButtonWidget(x, y, 20, 20, 0, 0, 20, REFRESH_BUTTON_LOCATION, 20, 40, buttonClick, renderTooltip, null);
    }

    @Contract(value = "_, _, _ -> new", pure = true)
    public static @NotNull ButtonWidget configureButton(int x, int y, Screen screen) {
        ButtonWidget.TooltipSupplier renderTooltip = (ButtonWidget b, MatrixStack ma, int mx, int my) -> screen.renderTooltip(ma, CONFIGURE_TOOLTIP, mx, my);
        ButtonWidget.PressAction buttonClick = b -> MinecraftClient.getInstance().setScreen(new ModMenuEntry().getModConfigScreenFactory().create(screen));

        return new TexturedButtonWidget(x, y, 20, 20, 0, 0, 20, CONFIGURE_BUTTON_LOCATION, 20, 40, buttonClick, renderTooltip, null);
    }

    public static List<Text> textListFromKey(String key) {
        return Arrays.stream(I18n.translate(key).split("\n")).map(x -> (Text) new LiteralText(x)).toList();
    }
}
