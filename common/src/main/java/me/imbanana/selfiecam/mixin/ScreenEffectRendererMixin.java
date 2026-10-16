package me.imbanana.selfiecam.mixin;

import me.imbanana.selfiecam.SelfiecamClient;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ScreenEffectRenderer.class)
public abstract class ScreenEffectRendererMixin {
    @ModifyVariable(
            method = "submit",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )

    private boolean hideHudWhenInCamera(boolean hideGui) {
        return hideGui || SelfiecamClient.getCameraController().isInSelfieMode();
    }
}
