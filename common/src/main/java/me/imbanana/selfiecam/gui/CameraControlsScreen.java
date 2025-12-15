package me.imbanana.selfiecam.gui;

import me.imbanana.selfiecam.ModCamera;
import me.imbanana.selfiecam.ModShaders;
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

    private int selectedShader;
    private double oldZoomValueSlider;

    private ZoomSliderWidget sliderWidget;
    private Button takePicButton;
    private Button testShaderButton;

    public void showOverlay() {
        this.isEnabled = true;
        init();
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

        this.selectedShader = -1;
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

        this.takePicButton = Button.builder(
                Component.literal("Take Pic"),
                btn -> SelfiecamClient.getCameraController().takePic(1440, 3088)
        )
                .pos(0, 30)
                .build();

        this.testShaderButton = Button.builder(
                        Component.literal("Camera Filter: " + (this.selectedShader == -1 ? "Normal" : ModShaders.shaderList.get(this.selectedShader).getPath())),
                        btn -> {
                            this.selectedShader++;
                            if (this.selectedShader >= ModShaders.shaderList.size()) {
                                this.selectedShader = -1;
                            }

                            String selected;
                            if (this.selectedShader == -1) {
                                Minecraft.getInstance().gameRenderer.clearPostEffect();
                                selected = "Normal";
                            } else {
                                Minecraft.getInstance().gameRenderer.setPostEffect(ModShaders.shaderList.get(this.selectedShader));
                                selected = ModShaders.shaderList.get(this.selectedShader).getPath();
                            }
                            btn.setMessage(Component.literal("Camera Filter: " + selected));
                        }
                )
                .pos(0, 70)
                .build();

        this.addRenderableWidget(this.sliderWidget);
        this.addRenderableWidget(this.takePicButton);
        this.addRenderableWidget(this.testShaderButton);
    }

    public void tick() {
        if (this.oldZoomValueSlider != this.sliderWidget.getValue()) {
            SelfiecamClient.getCameraController().setZoomPercentage(this.sliderWidget.getValue() / 100f);
        } else {
            this.sliderWidget.setValue(SelfiecamClient.getCameraController().getZoomPercentage() * 100);
        }
        this.oldZoomValueSlider = this.sliderWidget.getValue();
    }

    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        int xPos = (int) Minecraft.getInstance().mouseHandler.getScaledXPos(Minecraft.getInstance().getWindow());
        int yPos = (int) Minecraft.getInstance().mouseHandler.getScaledYPos(Minecraft.getInstance().getWindow());

        renderables.forEach(renderable -> renderable.render(guiGraphics, xPos, yPos, deltaTracker.getGameTimeDeltaPartialTick(false)));
    }

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
    public @NotNull List<? extends GuiEventListener> children() {
        return this.widgets;
    }

    public boolean canInteract() {
        return !Minecraft.getInstance().mouseHandler.isMouseGrabbed();
    }
}
