package me.imbanana.selfiecam.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import me.imbanana.selfiecam.SelfiecamClient;
import me.imbanana.selfiecam.gui.CameraControlsScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin extends ReentrantBlockableEventLoop<Runnable> implements WindowEventHandler {
    @Shadow
    @Final
    private Window window;

    @Shadow
    @Nullable
    public LocalPlayer player;

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

    @Inject(
            method = "startAttack()Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void injectStartAttack(CallbackInfoReturnable<Boolean> cir) {
        if (!SelfiecamClient.getCameraController().isInSelfieMode()) return;
        if (this.player.getMainArm() != SelfiecamClient.getPlayerController().getSelfieArm()) return;

        cir.setReturnValue(false);
    }

    @Definition(id = "hitResult", field = "Lnet/minecraft/client/Minecraft;hitResult:Lnet/minecraft/world/phys/HitResult;")
    @Expression("this.hitResult != null")
    @ModifyExpressionValue(
            method = "startUseItem()V",
            at = @At(
                    value = "MIXINEXTRAS:EXPRESSION",
                    ordinal = 1
            )
    )
    private boolean injectStartUseItem(boolean original, @Local InteractionHand interactionHand) {
        if (!SelfiecamClient.getCameraController().isInSelfieMode()) return original;

        boolean isMainArm = this.player.getMainArm() == SelfiecamClient.getPlayerController().getSelfieArm();
        boolean isUsingSelfieHand =
                (interactionHand == InteractionHand.MAIN_HAND && isMainArm) ||
                (interactionHand == InteractionHand.OFF_HAND && !isMainArm);

        return !isUsingSelfieHand && original;
    }

    @ModifyExpressionValue(
            method = "startUseItem()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z",
                    ordinal = 1
            )
    )
    private boolean injectStatUseItemPartTwo(boolean original, @Local InteractionHand interactionHand) {
        if (!SelfiecamClient.getCameraController().isInSelfieMode()) return original;

        boolean isMainArm = this.player.getMainArm() == SelfiecamClient.getPlayerController().getSelfieArm();
        boolean isUsingSelfieHand =
                (interactionHand == InteractionHand.MAIN_HAND && isMainArm) ||
                        (interactionHand == InteractionHand.OFF_HAND && !isMainArm);

        return original || isUsingSelfieHand;
    }
}
