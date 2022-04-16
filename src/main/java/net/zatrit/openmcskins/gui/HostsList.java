package net.zatrit.openmcskins.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.RenderShape;
import net.zatrit.openmcskins.OpenMCSkins;
import net.zatrit.openmcskins.resolvers.AbstractResolver;
import org.jetbrains.annotations.NotNull;

public class HostsList extends ObjectSelectionList<HostsList.HostEntry> {

    private final Screen parent;

    public HostsList(Minecraft minecraft, Screen parent, int i, int j, int k, int l) {
        super(minecraft, i, j, k, l, 20);
        this.parent = parent;

        for (AbstractResolver<?> resolver : OpenMCSkins.HOSTS)
            addEntry(new HostEntry(resolver));
    }

    @Override
    protected boolean isFocused() {
        return parent.getFocused() == this;
    }

    @Override
    protected void renderList(@NotNull PoseStack poseStack, int x, int y, int mouseX, int mouseY, float partialTick) {

    }

    public class HostEntry extends ObjectSelectionList.Entry<HostEntry> {
        private final String name;

        public HostEntry(@NotNull AbstractResolver<?> resolver) {
            this.name = resolver.getName();
        }

        @Override
        public void render(@NotNull PoseStack poseStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTick) {
            GuiComponent.drawString(poseStack, HostsList.this.minecraft.font, this.name, left + 5, top + 2, 0xFFFFFFFF);
        }

        @Override
        public Component getNarration() {
            return new TextComponent(name);
        }
    }
}
