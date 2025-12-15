package me.imbanana.selfiecam.mixin;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import me.imbanana.selfiecam.SelfiecamClient;
import me.imbanana.selfiecam.gui.CameraControlsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.entity.vehicle.Minecart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin extends ReentrantBlockableEventLoop<Runnable> implements WindowEventHandler {
    @Shadow
    @Final
    private Window window;

    public MinecraftMixin(String string) {
        super(string);
    }

    @Inject(
            method = "resizeDisplay()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/platform/Window;setGuiScale(I)V",
                    shift = At.Shift.AFTER
            )
    )
    private void resizeCameraControlsScreen(CallbackInfo ci) {
        CameraControlsScreen screen = SelfiecamClient.getCameraControlsGui();
        if(!screen.isEnabled()) return;

        int newWidth = this.window.getGuiScaledWidth();
        int newHeight = this.window.getGuiScaledHeight();

        screen.resize(newWidth, newHeight);
    }

    @Inject(
            method = "tick()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/DebugScreenOverlay;showDebugScreen()Z"
            )
    )
    private void tickCameraControls(CallbackInfo ci) {
        CameraControlsScreen screen = SelfiecamClient.getCameraControlsGui();
        if (!screen.isEnabled()) return;

        screen.tick();
    }
}
