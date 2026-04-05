package me.imbanana.selfiecam.gui;

import me.imbanana.selfiecam.SelfiecamClient;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;

import java.time.Duration;
import java.util.function.Supplier;

public class CapturePictureButton extends Button {
    private int captureWidth;
    private int captureHeight;

    public CapturePictureButton(int x, int y, int width, int height, int captureWidth, int captureHeight) {
        super(x, y, width, height, Component.translatable("gui.selfiecam.camera.capture"), (button) -> { }, Supplier::get);

        this.captureWidth = captureWidth;
        this.captureHeight = captureHeight;

        this.setTooltip(Tooltip.create(this.getMessage()));
        this.setTooltipDelay(Duration.ofMillis(500));
    }

    @Override
    public void onPress(InputWithModifiers inputWithModifiers) {
        SelfiecamClient.getCameraController().takePic(this.captureWidth, this.captureHeight);

        // Capture All Shaders For UI
//        ModShaders.SHADERS.forEach(shader -> {
//            Minecraft.getInstance().gameRenderer.setPostEffect(shader);
//            SelfiecamClient.getCameraController().takePic(74 * 4, 20 * 4);
//        });
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                SelfiecamClient.idOf("widget/capture_button" + (this.isHoveredOrFocused() ? "_highlight" : "")),
                this.getX(),
                this.getY(),
                this.getWidth(),
                this.getHeight(),
                ARGB.white(this.alpha)
        );
    }

    public void setCaptureHeight(int captureHeight) {
        this.captureHeight = captureHeight;
    }

    public void setCaptureWidth(int captureWidth) {
        this.captureWidth = captureWidth;
    }
}
