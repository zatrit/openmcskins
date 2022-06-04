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
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.config.Config;
import net.zatrit.openmcskins.gui.GUIUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Mixin(SkinOptionsScreen.class)
public abstract class SkinOptionsScreenMixin extends Screen {
    private static final String reloadConfigPattern = "text.autoconfig.openmcskins.option.reload.@Tooltip[%d]";
    private static final List<Text> reloadConfigButtonTooltip = IntStream.range(0, 2).mapToObj(reloadConfigPattern::formatted).map(x -> Text.of(I18n.translate(x))).toList();
    private static final List<Text> configureConfigButtonTooltip = Arrays.stream(I18n.translate("openmcskins.configure").split("\n")).parallel().map(Text::of).toList();
    private static final Identifier buttonsId = new Identifier(OpenMCSkins.MOD_ID, "textures/gui/gui_buttons.png");

    protected SkinOptionsScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    public void init(CallbackInfo info) {
        final int buttonX = this.width / 2 - 124;
        final int buttonY = this.height / 6 + 12 * (PlayerModelPart.values().length + 1);

        final ButtonWidget.PressAction optionsOnClick = b -> MinecraftClient.getInstance().setScreen(AutoConfig.getConfigScreen(Config.class, this).get());
        final ButtonWidget.PressAction refreshOnClick = b -> OpenMCSkins.invalidateAllResolvers();

        final ButtonWidget refreshButton = GUIUtils.createButton(buttonX - 24, buttonY, 0, this, reloadConfigButtonTooltip, buttonsId, refreshOnClick);
        final ButtonWidget optionsButton = GUIUtils.createButton(buttonX, buttonY, 20, this, configureConfigButtonTooltip, buttonsId, optionsOnClick);

        this.addDrawableChild(optionsButton);
        this.addDrawableChild(refreshButton);
    }
}
