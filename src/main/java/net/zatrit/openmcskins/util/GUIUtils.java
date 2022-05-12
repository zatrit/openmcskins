package net.zatrit.openmcskins.util;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public final class GUIUtils {
    private GUIUtils() {
    }

    public static @NotNull ButtonWidget createButton(int x, int y, Screen screen, List<Text> tooltip, Identifier resource, ButtonWidget.PressAction buttonClick) {
        ButtonWidget.TooltipSupplier renderTooltip = (ButtonWidget b, MatrixStack ma, int mx, int my) -> screen.renderTooltip(ma, tooltip, mx, my);
        return new TexturedButtonWidget(x, y, 20, 20, 0, 0, 20, resource, 20, 40, buttonClick, renderTooltip, null);
    }

    public static List<Text> textListFromKey(String key) {
        return Arrays.stream(I18n.translate(key).split("\n")).parallel().map(x -> (Text) new LiteralText(x)).toList();
    }
}
