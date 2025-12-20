package me.imbanana.selfiecam.gui;

import me.imbanana.selfiecam.ModCamera;
import me.imbanana.selfiecam.SelfiecamClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CameraControlsScreen extends AbstractContainerEventHandler {
    private boolean isEnabled = false;
    private final List<GuiEventListener> widgets = new ArrayList<>();
    private final List<Renderable> renderables = new ArrayList<>();
    private int width;
    private int height;

    private double oldZoomValueSlider;

    private ZoomSliderWidget sliderWidget;
    private CapturePictureButton capturePictureButton;
    private FilterToggleButton filterToggleButton;
    private FilterSelectionWidget filterSelectionWidget;
    private RatioSelectionWidget ratioSelectionWidget;

    public void showOverlay() {
        this.isEnabled = true;
        this.init();
    }

    public void hideOverlay() {
        this.isEnabled = false;
    }

    public boolean isEnabled() {
        return this.isEnabled
                && !Minecraft.getInstance().options.hideGui
                && !(Minecraft.getInstance().screen instanceof GameModeSwitcherScreen)
                && !(Minecraft.getInstance().screen instanceof PauseScreen);
    }

    private void init() {
        this.width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
        this.height = Minecraft.getInstance().getWindow().getGuiScaledHeight();

        this.oldZoomValueSlider = ModCamera.DEFAULT_ZOOM;

        this.buildWidgets();
    }

    private void buildWidgets() {
        widgets.clear();
        renderables.clear();

        this.sliderWidget = new ZoomSliderWidget(
                (width - 200) / 2,
                height - 30,
                200,
                20,
                Component.empty(),
                Component.empty(),
                100,
                0,
                SelfiecamClient.getCameraController().getZoomPercentage() * 100,
                0f,
                2,
                true
        );

        this.capturePictureButton = new CapturePictureButton(
                this.width - 42,
                this.height - 42,
                32,
                32,
                this.width,
                this.height
        );

        this.filterToggleButton = new FilterToggleButton(
                this.width - 42,
                this.height - 184,
                32,
                32,
                false
        );

        this.filterSelectionWidget = new FilterSelectionWidget(
                this.width - 80,
                this.height - 152,
                80,
                100
        );

        this.ratioSelectionWidget = new RatioSelectionWidget(
                this.width / 2,
                this.height / 2,
                this.width,
                this.height
        );

        this.addRenderableWidget(this.ratioSelectionWidget); // Need to be first
        this.addRenderableWidget(this.sliderWidget);
        this.addRenderableWidget(this.capturePictureButton);
        this.addRenderableWidget(this.filterToggleButton);
        this.addRenderableWidget(this.filterSelectionWidget);
    }

    public void tick() {
        if (this.oldZoomValueSlider != this.sliderWidget.getValue()) {
            SelfiecamClient.getCameraController().setZoomPercentage(this.sliderWidget.getValue() / 100f);
        } else {
            this.sliderWidget.setValue(SelfiecamClient.getCameraController().getZoomPercentage() * 100);
        }
        this.oldZoomValueSlider = this.sliderWidget.getValue();

        this.capturePictureButton.setCaptureWidth(this.ratioSelectionWidget.getWidth());
        this.capturePictureButton.setCaptureHeight(this.ratioSelectionWidget.getHeight());

        if (this.filterToggleButton.isOpen()) {
            this.filterSelectionWidget.show();
        } else {
            this.filterSelectionWidget.hide();
        }
    }

    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        int xPos = (int) Minecraft.getInstance().mouseHandler.getScaledXPos(Minecraft.getInstance().getWindow());
        int yPos = (int) Minecraft.getInstance().mouseHandler.getScaledYPos(Minecraft.getInstance().getWindow());

        renderables.forEach(renderable -> renderable.render(guiGraphics, xPos, yPos, deltaTracker.getGameTimeDeltaPartialTick(false)));
    }

    // TODO: fix screen not resizing when resizing app when in pause screen!
    public void resize(int width, int height) {
        this.width = width;
        this.height = height;

        this.buildWidgets();
    }

    private <T extends GuiEventListener & Renderable> void addRenderableWidget(T widget) {
        this.renderables.add(widget);
        this.widgets.add(widget);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        if (super.mouseClicked(mouseButtonEvent, bl)) return true;

        if (this.ratioSelectionWidget.mouseClicked(mouseButtonEvent, bl)) {
            this.setFocused(this.ratioSelectionWidget);
            if (mouseButtonEvent.button() == 0) {
                this.setDragging(true);
            }

            return true;
        }

        return false;
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return this.widgets;
    }

    public boolean canInteract() {
        return !Minecraft.getInstance().mouseHandler.isMouseGrabbed();
    }
}
