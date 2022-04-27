package net.zatrit.openmcskins.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.SkinOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.annotation.KeepClass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;

@KeepClass
@Mixin(SkinOptionsScreen.class)
public abstract class SkinOptionsScreenMixin extends GameOptionsScreen {
    private static final Identifier REFRESH_BUTTON_LOCATION = new Identifier(OpenMCSkins.MOD_ID, "textures/gui/refresh_button.png");
    private static final List<Text> RELOAD_CONFIG_TOOLTIP = Arrays.stream(I18n.translate("openmcskins.reloadConfig").split("\n")).map(x -> (Text) new LiteralText(x)).toList();

    private SkinOptionsScreenMixin(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo info) {
        // fun fact: if you inline all these variables, line 44 will take over 300 symbols
        ButtonWidget.TooltipSupplier renderTooltip = (ButtonWidget b, MatrixStack ma, int mx, int my) -> renderTooltip(ma, RELOAD_CONFIG_TOOLTIP, mx, my);
        ButtonWidget.PressAction buttonClick = b -> OpenMCSkins.reloadConfig();

        int buttonX = this.width / 2 - 124;
        int buttonY = this.height / 6 + 12 * (PlayerModelPart.values().length + 1);

        this.addDrawableChild(new TexturedButtonWidget(buttonX, buttonY, 20, 20, 0, 0, 20, REFRESH_BUTTON_LOCATION, 20, 40, buttonClick, renderTooltip, null));
    }
}
