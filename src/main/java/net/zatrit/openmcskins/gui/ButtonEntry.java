/* It's a modified version of ButtonEntry created by A5b84
 * It's licensed under LGPLv3, you can find copy of it
 * in NOTICE file
 * */

package net.zatrit.openmcskins.gui;

import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.zatrit.openmcskins.annotation.KeepClassMember;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ButtonEntry extends TooltipListEntry<Void> {

    private static final int HEIGHT = 20;
    private final ButtonWidget button;

    @SuppressWarnings({"deprecation", "UnstableApiUsage"})
    public ButtonEntry(Text fieldName, ButtonWidget.PressAction onPress) {
        super(fieldName, null);
        int width = MinecraftClient.getInstance().textRenderer.getWidth(fieldName) + 24;
        button = new ButtonWidget(0, 0, width, HEIGHT, fieldName, onPress);
    }

    @KeepClassMember
    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        button.x = x + (entryWidth - button.getWidth()) / 2;
        button.y = y + (entryHeight - HEIGHT) / 2;
        button.render(matrices, mouseX, mouseY, delta);
    }

    @KeepClassMember
    @Override
    public Void getValue() {
        return null;
    }

    @KeepClassMember
    @Override
    public Optional<Void> getDefaultValue() {
        return Optional.empty();
    }

    @KeepClassMember
    @Override
    public void save() {
    }

    private List<ButtonWidget> children0() {
        return Collections.singletonList(button);
    }

    @KeepClassMember
    @Override
    public List<? extends Element> children() {
        return children0();
    }

    @KeepClassMember
    @Override
    public List<? extends Selectable> narratables() {
        return children0();
    }

}