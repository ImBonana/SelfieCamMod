package me.imbanana.selfiecam.mixin;

import me.imbanana.selfiecam.SelfiecamClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow
    protected abstract void renderChat(GuiGraphics guiGraphics, DeltaTracker deltaTracker);

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(
            method = "render",
            at = @At("HEAD"),
            cancellable = true
    )
    private void checkSelfieMode(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (this.minecraft.screen instanceof LevelLoadingScreen) return;
        if (SelfiecamClient.getCameraController().shouldHideGUI()) {
            ci.cancel();
            return;
        };

        // Render Camera Controls
        if (SelfiecamClient.getCameraControlsGui().isEnabled()) {
            SelfiecamClient.getCameraControlsGui().render(guiGraphics, deltaTracker);
        }

        // Hide player overlays
        if (SelfiecamClient.getCameraController().isInSelfieMode()) {
             ci.cancel();

             // keep the chat
             if (!this.minecraft.options.hideGui) {
                 this.renderChat(guiGraphics, deltaTracker);
             }
        }
    }
}
