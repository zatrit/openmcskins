package net.zatrit.openmcskins.mod.mixin;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.SkinOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.mod.OpenMCSkins;
import net.zatrit.openmcskins.config.OpenMCSkinsConfig;
import net.zatrit.openmcskins.util.GUIUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

import static net.zatrit.openmcskins.util.GUIUtils.textListFromKey;

@Mixin(SkinOptionsScreen.class)
public abstract class SkinOptionsScreenMixin extends Screen {
    private static final Identifier REFRESH_BUTTON_LOCATION = new Identifier(OpenMCSkins.MOD_ID, "textures/gui/refresh_button.png");
    private static final List<Text> REFRESH_CONFIG_BUTTON_TOOLTIP = textListFromKey("openmcskins.reloadConfig");
    private static final Identifier CONFIGURE_BUTTON_LOCATION = new Identifier(OpenMCSkins.MOD_ID, "textures/gui/configure_button.png");
    private static final List<Text> CONFIGURE_BUTTON_TOOLTIP = textListFromKey("openmcskins.configure");

    protected SkinOptionsScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void init(CallbackInfo info) {
        int buttonX = this.width / 2 - 124;
        int buttonY = this.height / 6 + 12 * (PlayerModelPart.values().length + 1);

        ButtonWidget.PressAction optionsOnClick = b -> MinecraftClient.getInstance().setScreen(AutoConfig.getConfigScreen(OpenMCSkinsConfig.class, this).get());
        ButtonWidget.PressAction refreshOnClick = b -> OpenMCSkins.reloadConfig();

        ButtonWidget optionsButton = GUIUtils.createButton(buttonX, buttonY, this, CONFIGURE_BUTTON_TOOLTIP, CONFIGURE_BUTTON_LOCATION, optionsOnClick);
        ButtonWidget refreshButton = GUIUtils.createButton(buttonX - 24, buttonY, this, REFRESH_CONFIG_BUTTON_TOOLTIP, REFRESH_BUTTON_LOCATION, refreshOnClick);

        this.addDrawableChild(optionsButton);
        this.addDrawableChild(refreshButton);
    }
}
