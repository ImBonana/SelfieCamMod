package me.imbanana.selfiecam.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.Window;
import me.imbanana.selfiecam.ModCamera;
import me.imbanana.selfiecam.SelfiecamClient;
import me.imbanana.selfiecam.gui.CameraControlsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {
    @Shadow
    public abstract double getScaledXPos(Window window);

    @Shadow
    public abstract double getScaledYPos(Window window);

    @Shadow
    protected abstract MouseButtonInfo simulateRightClick(MouseButtonInfo mouseButtonInfo, boolean bl);

    @Shadow
    protected int lastClickButton;

    @Shadow
    private double accumulatedDX;

    @Shadow
    private double accumulatedDY;

    @Shadow
    private @Nullable MouseButtonInfo activeButton;

    @Shadow
    private double mousePressedTime;

    @Shadow
    @Nullable
    private MouseHandler.LastClick lastClick;

    @Inject(
           method = "onScroll",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"
            ),
            cancellable = true
    )
    private void injectOnScroll(long l, double d, double e, CallbackInfo ci, @Local Vector2i scroll, @Local(ordinal = 3) double scrollX, @Local(ordinal = 4) double scrollY) {
        Window window = Minecraft.getInstance().getWindow();

        ModCamera cameraController = SelfiecamClient.getCameraController();
        CameraControlsScreen screen = SelfiecamClient.getCameraControlsGui();

        if (cameraController.isInSelfieMode() && (!screen.isEnabled() || !screen.canInteract())) {
            cameraController.handleMouseScroll(scroll);
            ci.cancel();
        }

        if (screen.isEnabled() && screen.canInteract()) {
            double mouseX = this.getScaledXPos(window);
            double mouseY = this.getScaledYPos(window);

            screen.mouseScrolled(mouseX, mouseY, scrollX, scrollY) ;
        }
    }

    @Definition(id = "mouseGrabbed", field = "Lnet/minecraft/client/MouseHandler;mouseGrabbed:Z")
    @Expression("this.mouseGrabbed")
    @ModifyExpressionValue(
            method = "onButton",
            at = @At(
                    value = "MIXINEXTRAS:EXPRESSION",
                    ordinal = 0
            )
    )
    private boolean checkSelfieMode(boolean original) {
         return original || SelfiecamClient.getCameraController().isInSelfieMode();
    }

    @Inject(
            method = "onButton",
            at = @At("TAIL")
    )
    private void checkCameraControlsOverlayMouseButton(long l, MouseButtonInfo mouseButtonInfo, int i, CallbackInfo ci) {
        Window window = Minecraft.getInstance().getWindow();
        if (window.handle() != l) return;

        CameraControlsScreen screen = SelfiecamClient.getCameraControlsGui();
        if (!screen.isEnabled()) return;
        if (!screen.canInteract()) return;

        double mouseX = this.getScaledXPos(window);
        double mouseY = this.getScaledYPos(window);

        MouseButtonInfo buttonInfo = this.simulateRightClick(mouseButtonInfo, i == 1);

        MouseButtonEvent mouseButtonEvent = new MouseButtonEvent(mouseX, mouseY, buttonInfo);

        if (i == 1) {
            long currentTime = Util.getMillis();

            boolean isQuickClick = this.lastClick != null && currentTime - this.lastClick.time() < 250L && this.lastClickButton == mouseButtonEvent.button();
            if (screen.mouseClicked(mouseButtonEvent, isQuickClick)) {
                this.lastClick = new MouseHandler.LastClick(currentTime, lastClick.screen());
                this.lastClickButton = buttonInfo.button();
            }
        } else {
            screen.mouseReleased(mouseButtonEvent);
        }
    }

    @Inject(
            method = "handleAccumulatedMovement",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/MouseHandler;isMouseGrabbed()Z"
            )
    )
    private void checkCameraControlsOverlayMouseMoved(CallbackInfo ci) {
        CameraControlsScreen screen = SelfiecamClient.getCameraControlsGui();
        if (!screen.isEnabled()) return;
        if (!screen.canInteract()) return;

        Window window = Minecraft.getInstance().getWindow();
        boolean moved = this.accumulatedDX != 0.0 || this.accumulatedDY != 0.0;

        if (moved) {
            double mouseX = this.getScaledXPos(window);
            double mouseY = this.getScaledYPos(window);

            screen.mouseMoved(mouseX, mouseY);

            if (this.activeButton != null && this.mousePressedTime > 0.0) {
                double accumulatedDeltaMouseX = this.getScaledXPos(window);
                double accumulatedDeltaMouseY = this.getScaledYPos(window);

                screen.mouseDragged(new MouseButtonEvent(mouseX, mouseY, this.activeButton), accumulatedDeltaMouseX, accumulatedDeltaMouseY);
            }
        }
    }
}
