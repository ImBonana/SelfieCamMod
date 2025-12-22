package me.imbanana.selfiecam.gui;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import me.imbanana.selfiecam.SelfiecamClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

import java.time.Duration;
import java.util.function.Supplier;

public class FilterToggleButton extends Button {
    private boolean isOpen;

    protected FilterToggleButton(int x, int y, int width, int height, boolean isOpen) {
        super(x, y, width, height, Component.translatable("gui.selfiecam.camera.filter"), button -> { }, Supplier::get);

        this.isOpen = isOpen;

        this.setTooltip(Tooltip.create(this.getMessage()));
        this.setTooltipDelay(Duration.ofMillis(500));
    }

    public boolean isOpen() {
        return isOpen;
    }

    @Override
    public void onPress(InputWithModifiers inputWithModifiers) {
        this.isOpen = !this.isOpen;
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int i, int j, float f) {
        guiGraphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                SelfiecamClient.idOf("widget/filter_toggle_button" + (this.isHoveredOrFocused() || this.isOpen ? "_highlight" : "")),
                this.getX(),
                this.getY(),
                this.getWidth(),
                this.getHeight(),
                ARGB.white(this.alpha)
        );
    }
}
