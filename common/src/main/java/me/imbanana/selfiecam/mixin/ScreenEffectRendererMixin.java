package me.imbanana.selfiecam.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.imbanana.selfiecam.ModCamera;
import me.imbanana.selfiecam.SelfiecamClient;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ScreenEffectRenderer.class)
public abstract class ScreenEffectRendererMixin {
    @ModifyExpressionValue(
            method = "renderScreenEffect",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/Options;hideGui:Z",
                    opcode = Opcodes.GETFIELD
            )
    )
    private boolean checkSelfieCameraOutline(boolean original) {
        return original || SelfiecamClient.getCameraController().isInSelfieMode();
    }
}
