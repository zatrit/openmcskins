package net.zatrit.openmcskins.mod.mixin;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.SkinOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.config.Config;
import net.zatrit.openmcskins.gui.GUIUtils;
import net.zatrit.openmcskins.mod.OpenMCSkins;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.stream.IntStream;

import static net.zatrit.openmcskins.gui.GUIUtils.textListFromKey;

@Mixin(SkinOptionsScreen.class)
public abstract class SkinOptionsScreenMixin extends Screen {
    private static final Identifier BUTTONS_LOCATION = new Identifier(OpenMCSkins.MOD_ID, "textures/gui/gui_buttons.png");
    private static final List<Text> CONFIGURE_BUTTON_TOOLTIP = textListFromKey("openmcskins.configure");

    protected SkinOptionsScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void init(CallbackInfo info) {
        int buttonX = this.width / 2 - 124;
        int buttonY = this.height / 6 + 12 * (PlayerModelPart.values().length + 1);

        ButtonWidget.PressAction optionsOnClick = b -> MinecraftClient.getInstance().setScreen(AutoConfig.getConfigScreen(Config.class, this).get());
        ButtonWidget optionsButton = GUIUtils.createButton(buttonX, buttonY, 0, this, CONFIGURE_BUTTON_TOOLTIP, BUTTONS_LOCATION, optionsOnClick);
        this.addDrawableChild(optionsButton);
    }
}
