package net.zatrit.openmcskins.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class GUIUtils {
    private GUIUtils() {
    }

    public static @NotNull ButtonWidget createButton(int x, int y, int textureUOffset, Screen screen, List<Text> tooltip, Identifier resource, ButtonWidget.PressAction buttonClick) {
        ButtonWidget.TooltipSupplier renderTooltip = (ButtonWidget b, MatrixStack ma, int mx, int my) -> screen.renderTooltip(ma, tooltip, mx, my);
        return new VanillaLikeTexturedButtonWidget(x, y, 20, 20, textureUOffset, 0, resource, 40, 20, buttonClick, renderTooltip);
    }
}
