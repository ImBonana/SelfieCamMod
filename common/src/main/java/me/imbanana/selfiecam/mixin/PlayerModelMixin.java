package me.imbanana.selfiecam.mixin;

import me.imbanana.selfiecam.SelfiecamClient;
import me.imbanana.selfiecam.accessors.IAvatarRenderState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin extends HumanoidModel<AvatarRenderState> {
    public PlayerModelMixin(ModelPart modelPart) {
        super(modelPart);
    }

    @Inject(
            method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/model/HumanoidModel;setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void injectSetupAnim(AvatarRenderState avatarRenderState, CallbackInfo ci) {
        if (!(avatarRenderState instanceof IAvatarRenderState renderState)) return;
        if (!renderState.selfieCam$isCameraEntity()) return;
        if (!SelfiecamClient.getCameraController().isInSelfieMode()) return;

        SelfiecamClient.getPlayerController().setupPlayerHand(rightArm, leftArm, head);
    }
}
