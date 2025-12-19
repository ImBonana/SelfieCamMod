package me.imbanana.selfiecam.gui;

import com.mojang.blaze3d.platform.cursor.CursorType;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import me.imbanana.selfiecam.SelfiecamClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import org.lwjgl.glfw.GLFW;

public class RatioSelectionWidget extends AbstractWidget {
    private int screenWidth;
    private int screenHeight;

    private ResizeMode resizeMode = ResizeMode.NONE;

    public RatioSelectionWidget(int width, int height, int screenWidth, int screenHeight) {
        super(0, 0, width, height, Component.translatable("gui.selfiecam.camera.select_ratio"));

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        this.reposition(screenWidth, screenHeight);
    }

    private void reposition(int screenWidth, int screenHeight) {
        this.setX((screenWidth - this.width) / 2);
        this.setY((screenHeight - this.height) / 2);

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public void resize(int width, int height) {
        this.setWidth(Math.clamp(width, 8, screenWidth));
        this.setHeight(Math.clamp(height, 8, screenHeight));

        this.reposition(screenWidth, screenHeight);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {
        guiGraphics.blitSprite(
                RenderPipelines.GUI_TEXTURED,
                SelfiecamClient.idOf("widget/ratio_selection"),
                this.getX(),
                this.getY(),
                this.getWidth(),
                this.getHeight()
        );

        int backgroundColor = ARGB.color(127, 0, 0,0);

        guiGraphics.fill(0, 0, guiGraphics.guiWidth(), this.getY(), backgroundColor);
        guiGraphics.fill(0, this.getBottom(), guiGraphics.guiWidth(), guiGraphics.guiHeight(), backgroundColor);
        guiGraphics.fill(0, this.getY(), this.getX(), this.getBottom(), backgroundColor);
        guiGraphics.fill(this.getRight(), this.getY(), guiGraphics.guiWidth(), this.getBottom(), backgroundColor);

        ResizeMode availableResizeMode = this.resizeMode == ResizeMode.NONE ? getAvailableMode(i, j) : this.resizeMode;

        if (availableResizeMode == ResizeMode.HORIZONTAL) {
            guiGraphics.requestCursor(CursorTypes.RESIZE_EW);
        } else if (availableResizeMode == ResizeMode.VERTICAL) {
            guiGraphics.requestCursor(CursorTypes.RESIZE_NS);
        } else if (availableResizeMode == ResizeMode.NORTH_WEST) {
            guiGraphics.requestCursor(CursorType.createStandardCursor(GLFW.GLFW_RESIZE_NWSE_CURSOR, "resize_nwse", CursorType.DEFAULT));
        } else if (availableResizeMode == ResizeMode.NORTH_EAST) {
            guiGraphics.requestCursor(CursorType.createStandardCursor(GLFW.GLFW_RESIZE_NESW_CURSOR, "resize_nesw", CursorType.DEFAULT));
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        // IDK
    }

    private ResizeMode getAvailableMode(double mouseX, double mouseY) {
        double range = 5;

        boolean isMouseInLeftSide = this.areCoordinatesInRectangle(
                mouseX,
                mouseY,
                this.getX() - range,
                this.getY() - range,
                this.getX() + range,
                this.getBottom() + range
        );

        boolean isMouseInRightSide = this.areCoordinatesInRectangle(
                mouseX,
                mouseY,
                this.getRight() - range,
                this.getY() - range,
                this.getRight() + range,
                this.getBottom() + range
        );

        boolean isMouseInTopSide = this.areCoordinatesInRectangle(
                mouseX,
                mouseY,
                this.getX() - range,
                this.getY() - range,
                this.getRight() + range,
                this.getY() + range
        );

        boolean isMouseInBottomSide = this.areCoordinatesInRectangle(
                mouseX,
                mouseY,
                this.getX() - range,
                this.getBottom() - range,
                this.getRight() + range,
                this.getBottom() + range
        );

        boolean isInTopLeft = isMouseInTopSide && isMouseInLeftSide;
        boolean isInTopRight = isMouseInTopSide && isMouseInRightSide;
        boolean isInBottomLeft = isMouseInBottomSide && isMouseInLeftSide;
        boolean isInBottomRight = isMouseInBottomSide && isMouseInRightSide;

        if (isInTopLeft || isInBottomRight) return ResizeMode.NORTH_WEST;
        if (isInTopRight || isInBottomLeft) return ResizeMode.NORTH_EAST;
        if (isMouseInLeftSide || isMouseInRightSide) return ResizeMode.HORIZONTAL;
        if (isMouseInTopSide || isMouseInBottomSide) return ResizeMode.VERTICAL;

        return ResizeMode.NONE;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (!this.isActive()) return false;
        this.resizeMode = getAvailableMode(mouseButtonEvent.x(), mouseButtonEvent.y());

        if (this.resizeMode != ResizeMode.NONE) {
            this.onClick(mouseButtonEvent, bl);
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            return true;
        }

        return false;
    }

    @Override
    public void onRelease(MouseButtonEvent mouseButtonEvent) {
        this.resizeMode = ResizeMode.NONE;
    }

    @Override
    protected void onDrag(MouseButtonEvent mouseButtonEvent, double d, double e) {
        boolean horizontal = this.resizeMode == ResizeMode.HORIZONTAL || this.resizeMode == ResizeMode.NORTH_EAST || this.resizeMode == ResizeMode.NORTH_WEST;
        boolean vertical = this.resizeMode == ResizeMode.VERTICAL || this.resizeMode == ResizeMode.NORTH_EAST || this.resizeMode == ResizeMode.NORTH_WEST;

        resizeFromMouse((int) mouseButtonEvent.x() , (int) mouseButtonEvent.y(), horizontal, vertical);
    }

    private void resizeFromMouse(int x, int y, boolean horizontal, boolean vertical) {
        this.resize(horizontal ? Math.abs(x - this.screenWidth / 2) * 2 : this.width, vertical ? Math.abs(y - this.screenHeight / 2) * 2 : this.height);
    }

    private boolean areCoordinatesInRectangle(double x, double y, double rectX, double rectY, double rectEndX, double rectEndY) {
        return x >= rectX && y >= rectY && x < rectEndX && y < rectEndY;
    }

    private enum ResizeMode {
        NORTH_WEST,
        NORTH_EAST,
        HORIZONTAL,
        VERTICAL,
        NONE
    }
}
