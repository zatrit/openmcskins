package net.zatrit.openmcskins.mod.mixin;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.gui.screen.option.SkinOptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.config.Config;
import net.zatrit.openmcskins.gui.GUIUtils;
import net.zatrit.openmcskins.io.Cache;
import net.zatrit.openmcskins.io.CacheCleanupResourceReload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;

@Mixin(SkinOptionsScreen.class)
public abstract class SkinOptionsScreenMixin extends Screen {
    private static final List<Text> reloadConfigButtonTooltip = Arrays.stream(I18n.translate("openmcskins.reload").split("\n")).parallel().map(Text::of).toList();
    private static final List<Text> clearCacheButtonTooltip = Arrays.stream(I18n.translate("openmcskins.clearCache").split("\n")).parallel().map(Text::of).toList();
    private static final List<Text> configureConfigButtonTooltip = Arrays.stream(I18n.translate("openmcskins.configure").split("\n")).parallel().map(Text::of).toList();
    private static final Identifier buttonsId = new Identifier(OpenMCSkins.MOD_ID, "textures/gui/gui_buttons.png");

    protected SkinOptionsScreenMixin(Text title) {
        super(title);
    }

    @SuppressWarnings("ConstantConditions")
    @Inject(method = "init", at = @At("RETURN"))
    public void init(CallbackInfo info) {
        final int buttonX = this.width / 2 - 124;
        final int buttonY = this.height / 6 + 24 * ((int) Math.floor(PlayerModelPart.values().length / 2.0F) + 1);

        final ButtonWidget.PressAction optionsOnClick = b -> MinecraftClient.getInstance().setScreen(AutoConfig.getConfigScreen(Config.class, this).get());
        final ButtonWidget.PressAction refreshOnClick = b -> OpenMCSkins.invalidateAllResolvers();
        final ButtonWidget.PressAction clearCacheOnClick = b -> client.setOverlay(new SplashOverlay(client, new CacheCleanupResourceReload(() -> Arrays.stream(Cache.values()).map(Cache::getCache)), OpenMCSkins::handleError, true));

        this.addDrawableChild(GUIUtils.createButton(buttonX - 24, buttonY, 0, this, reloadConfigButtonTooltip, buttonsId, refreshOnClick));
        this.addDrawableChild(GUIUtils.createButton(buttonX, buttonY, 20, this, configureConfigButtonTooltip, buttonsId, optionsOnClick));
        this.addDrawableChild(GUIUtils.createButton(width / 2 + 104, buttonY, 40, this, clearCacheButtonTooltip, buttonsId, clearCacheOnClick));
    }
}
