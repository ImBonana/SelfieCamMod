package me.imbanana.selfiecam.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.imbanana.selfiecam.ModCamera;
import me.imbanana.selfiecam.SelfiecamClient;
import net.minecraft.client.gui.components.ChatComponent;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin {
    @ModifyExpressionValue(
            method = "handleChatQueueClicked",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/Options;hideGui:Z",
                    opcode = Opcodes.GETFIELD
            )
    )
    private boolean checkSelfieCamera(boolean original) {
        return original || SelfiecamClient.getCameraController().isInSelfieMode();
    }
}
