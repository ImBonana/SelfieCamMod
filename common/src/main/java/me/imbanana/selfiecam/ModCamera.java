package me.imbanana.selfiecam;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import me.imbanana.selfiecam.mixin.CameraAccessor;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import org.joml.Vector2i;

import java.io.File;
import java.io.IOException;

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

    public void takePic(int width, int height) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget renderTarget = minecraft.getMainRenderTarget();


        int startX = (renderTarget.width - width) / 2;
        int startY = (renderTarget.height - height) / 2;

        try {
            this.shouldHideGUI = true;
            minecraft.gameRenderer.render(DeltaTracker.ONE, true);

            GpuTexture gpuTexture = renderTarget.getColorTexture();

            if (gpuTexture == null) throw new IllegalStateException("Tried to capture screenshot of an incomplete framebuffer");
            GpuBuffer gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "Picture Buffer",9, renderTarget.width * renderTarget.height * gpuTexture.getFormat().pixelSize());

            CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
            RenderSystem.getDevice().createCommandEncoder().copyTextureToBuffer(gpuTexture, gpuBuffer, 0, () -> {
                try (GpuBuffer.MappedView mappedView = commandEncoder.mapBuffer(gpuBuffer, true, false)) {
                    NativeImage nativeImage = new NativeImage(width, height, false);

                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            int color = mappedView.data().getInt((startX + x + (startY + y) * renderTarget.width) * gpuTexture.getFormat().pixelSize());
                            nativeImage.setPixelABGR(x, height - y - 1, color | 0xFF000000);
                        }
                    }

                    File picDir = new File(minecraft.gameDirectory, "pictures");
                    picDir.mkdir();
                    File picFile = getFile(picDir);

                    Util.ioPool().execute(() -> {
                        try {
                            nativeImage.writeToFile(picFile);
                            minecraft.execute(() ->
                                    minecraft.gui.getChat().addMessage(
                                            Component.literal(picFile.getName())
                                                    .withStyle(ChatFormatting.UNDERLINE)
                                                    .withStyle(style ->
                                                            style.withClickEvent(new ClickEvent.OpenFile(picFile.getAbsoluteFile()))
                                                    )
                                    )
                            );
                        } catch (IOException e) {
                            SelfiecamClient.LOGGER.error("Couldn't save the picture", e);
                        }

                        nativeImage.close();
                    });
                }

                gpuBuffer.close();
            }, 0);
        } catch (Exception e) {
            SelfiecamClient.LOGGER.error("Couldn't save the picture", e);
        } finally {
            this.shouldHideGUI = false;
        }
    }

    private static File getFile(File file) {
        String string = Util.getFilenameFormattedDateTime();
        int i = 1;

        while (true) {
            File file2 = new File(file, string + (i == 1 ? "" : "_" + i) + ".png");
            if (!file2.exists()) {
                return file2;
            }

            i++;
        }
    }
}