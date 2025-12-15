package me.imbanana.selfiecam.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.imbanana.selfiecam.ModCamera;
import me.imbanana.selfiecam.SelfiecamClient;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin implements ResourceManagerReloadListener, AutoCloseable {
    @ModifyExpressionValue(
            method = "extractVisibleEntities",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;isDetached()Z"
            )
    )
    private boolean checkSelfieCamera(boolean original) {
        // Render player model
        return original || SelfiecamClient.getCameraController().isInSelfieMode();
    }
}
