package me.imbanana.selfiecam.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import me.imbanana.selfiecam.ModCamera;
import me.imbanana.selfiecam.SelfiecamClient;
import net.minecraft.client.Camera;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.waypoints.TrackedWaypoint;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin implements TrackedWaypoint.Camera {
    @Inject(
            method = "setup",
            at = @At(
                    value = "TAIL"
            )
    )
    private void injectSetup(BlockGetter blockGetter, Entity entity, boolean bl, boolean bl2, float f, CallbackInfo ci) {
        if(SelfiecamClient.getCameraController().isInSelfieMode() && !bl) {
            SelfiecamClient.getCameraController().handleCamera((CameraAccessor) this);
        }
    }
}
