package me.imbanana.selfiecam.mixin;

import me.imbanana.selfiecam.SelfiecamClient;
import net.minecraft.client.Camera;
import net.minecraft.world.waypoints.TrackedWaypoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin implements TrackedWaypoint.Camera {
    @Shadow
    public abstract boolean isDetached();

    @Inject(
            method = "alignWithEntity",
            at = @At(
                    value = "TAIL"
            )
    )
    private void injectAlignWithEntity(float partialTicks, CallbackInfo ci) {
        if(SelfiecamClient.getCameraController().isInSelfieMode() && !this.isDetached()) {
            SelfiecamClient.getCameraController().handleCamera((CameraAccessor) this);
        }
    }
}
