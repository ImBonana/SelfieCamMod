package me.imbanana.selfiecam.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.imbanana.selfiecam.SelfiecamClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.waypoints.TrackedWaypoint;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements TrackedWaypoint.Projector, AutoCloseable {
    @Shadow
    @Final
    private List<Identifier> requestedPostEffects;

    @ModifyExpressionValue(
            method = "renderItemInHand",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/CameraType;isFirstPerson()Z"
            )
    )
    private boolean checkSelfieCamera(boolean original) {
        // Hide player hand
        return original && !SelfiecamClient.getCameraController().isInSelfieMode();
    }

    @ModifyExpressionValue(
            method = "shouldRenderBlockOutline",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/Hud;isHidden()Z"
            )
    )
    private boolean checkSelfieCameraOutline(boolean original) {
        // Hide block outline
        return original || SelfiecamClient.getCameraController().isInSelfieMode();
    }

    @Inject(
            method = "update",
            at = @At("TAIL")
    )
    private void addCameraEffects(DeltaTracker deltaTracker, CallbackInfo ci) {
        Identifier cameraEffect = SelfiecamClient.getCameraController().getCameraEffect();

        if (cameraEffect != null) {
            this.requestedPostEffects.add(cameraEffect);
        }
    }

//    @ModifyExpressionValue(
//            method = "renderLevel",
//            at = @At(
//                    value = "FIELD",
//                    target = "Lnet/minecraft/client/Options;hideGui:Z",
//                    opcode = Opcodes.GETFIELD
//            )
//    )
//    private boolean checkSelfieCameraCrosshair(boolean original) {
//        // Hide player 3d crosshair
//        return original || SelfiecamClient.getCameraController().isInSelfieMode();
//    }
}
