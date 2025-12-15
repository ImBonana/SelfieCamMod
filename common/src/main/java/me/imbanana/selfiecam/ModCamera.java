package me.imbanana.selfiecam;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Window;
import me.imbanana.selfiecam.mixin.CameraAccessor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import org.joml.Vector2i;

public class ModCamera {
    public static final float MIN_CAMERA_ANGLE = -90f;
    public static final float MAX_CAMERA_ANGLE = 35f;

    private final double MAX_ZOOM = 1.3;
    private final double MIN_ZOOM = 0.8;

    // Percentage
    public static final double DEFAULT_ZOOM = 0.5;

    private boolean isInSelfieMode = false;
    private boolean shouldHideGUI = false;
    private double zoomPercentage = DEFAULT_ZOOM;

    public void setInSelfieMode(boolean value) {
        isInSelfieMode = value;
    }

    public boolean isInSelfieMode() {
        return isInSelfieMode;
    }

    public void setZoomPercentage(double value) {
        this.zoomPercentage = value;
    }

    public double getZoomPercentage() {
        return this.zoomPercentage;
    }

    public boolean shouldHideGUI() {
        return this.shouldHideGUI;
    }

    public void handleCamera(CameraAccessor camera) {

        float xRot = camera.selfieCam$getXRot();
        float yRot = camera.selfieCam$getYRot();

        float finalYRot = yRot + 180F;
        float finalXRot = -Math.clamp(xRot, MIN_CAMERA_ANGLE, MAX_CAMERA_ANGLE);

        camera.selfieCam$setRotation(finalYRot, finalXRot);
        camera.selfieCam$move(-camera.selfieCam$getMaxZoom((float) calcZoom(this.zoomPercentage)), 0, 0);

    }

    public void handleMouseScroll(Vector2i scroll) {
        float zoomAmount = -scroll.y / 10f;
        zoomPercentage = Math.clamp(zoomPercentage + zoomAmount, 0, 1f);
    }

    private double calcZoom(double percentage) {
        float fov = Minecraft.getInstance().options.fov().get();

        double maxZoom = MAX_ZOOM - (fov / 90f) * 0.1;

        return MIN_ZOOM + (maxZoom - MIN_ZOOM) * percentage;
    }

    public void takePic(int targetResWidth, int targetResHeight) {
        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();

        int width = window.getWidth();
        int height = window.getHeight();

        try {
            this.shouldHideGUI = true;

            RenderTarget renderTarget = minecraft.getMainRenderTarget();
            window.setWidth(targetResWidth);
            window.setHeight(targetResHeight);
            renderTarget.resize(targetResWidth, targetResHeight);

            minecraft.gameRenderer.render(DeltaTracker.ONE, true);

            Screenshot.grab(minecraft.gameDirectory, renderTarget, text -> minecraft.execute(() -> minecraft.gui.getChat().addMessage(text)));
        } catch (Exception e) {
            SelfiecamClient.LOGGER.error("Couldn't save the picture", e);
        } finally {
            window.setWidth(width);
            window.setHeight(height);
            minecraft.getMainRenderTarget().resize(width, height);
            this.shouldHideGUI = false;
        }
    }
}