package net.zatrit.openmcskins.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class OptionsScreen extends Screen {
    private final Screen previous;

    public OptionsScreen(Screen previous) {
        super(new TextComponent("Test"));
        this.previous = previous;
    }

    @Override
    protected void init() {
        this.addWidget(new HostsList(minecraft, this, 10, 10, width - 10, height - 10));
    }

    @Override
    public void onClose() {
        Objects.requireNonNull(this.minecraft).setScreen(previous);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int i, int j, float f) {
        renderBackground(poseStack);
        super.render(poseStack, i, j, f);
    }
}
