package net.zatrit.openmcskins.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.zatrit.openmcskins.annotation.KeepClassMember;

// I just installed a custom GUI resourcepack, and I want to fix button contrast with gui

public class VanillaLikeTexturedButtonWidget extends ButtonWidget {
    private final Identifier texture;
    private final int u;
    private final int v;
    private final int textureWidth;
    private final int textureHeight;

    public VanillaLikeTexturedButtonWidget(int x, int y, int width, int height, int u, int v, Identifier texture, int textureWidth, int textureHeight, ButtonWidget.PressAction pressAction, ButtonWidget.TooltipSupplier tooltipSupplier) {
        super(x, y, width, height, Text.of(""), pressAction, tooltipSupplier);
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.u = u;
        this.v = v;
        this.texture = texture;
    }

    @KeepClassMember
    public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.renderButton(matrices, mouseX, mouseY, delta);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, this.texture);
        RenderSystem.enableDepthTest();
        drawTexture(matrices, this.x, this.y, this.u, this.v, this.width, this.height, this.textureWidth, this.textureHeight);
        if (this.hovered) {
            this.renderTooltip(matrices, mouseX, mouseY);
        }
    }
}
