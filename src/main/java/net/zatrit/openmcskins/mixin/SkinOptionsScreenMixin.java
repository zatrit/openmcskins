package net.zatrit.openmcskins.mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.SkinOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.text.Text;
import net.zatrit.openmcskins.gui.GUIMaker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkinOptionsScreen.class)
public abstract class SkinOptionsScreenMixin extends GameOptionsScreen {
    private SkinOptionsScreenMixin(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @Inject(method = "init", at = @At("RETURN"))
    private void init(CallbackInfo info) {
        int buttonX = this.width / 2 - 124;
        int buttonY = this.height / 6 + 12 * (PlayerModelPart.values().length + 1);

        this.addDrawableChild(GUIMaker.createConfigureButton(buttonX, buttonY, this));
        this.addDrawableChild(GUIMaker.createRefreshConfigButton(buttonX - 24, buttonY, this));
    }
}
