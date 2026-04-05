package me.imbanana.selfiecam.mixin;

import com.mojang.blaze3d.platform.Window;
import me.imbanana.selfiecam.SelfiecamClient;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    protected abstract void extractChat(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker);

    @Inject(
            method = "extractRenderState",
            at = @At("HEAD"),
            cancellable = true
    )
    private void checkSelfieMode(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (this.minecraft.screen instanceof LevelLoadingScreen) return;
        if (SelfiecamClient.getCameraController().shouldHideGUI()) {
            ci.cancel();
            return;
        };

        // Render Camera Controls
        if (SelfiecamClient.getCameraControlsGui().isEnabled()) {
            Window window = this.minecraft.getWindow();
            int mouseX = Mth.floor(this.minecraft.mouseHandler.getScaledXPos(window));
            int mouseY = Mth.floor(this.minecraft.mouseHandler.getScaledYPos(window));
            graphics.nextStratum();
            SelfiecamClient.getCameraControlsGui().extractRenderState(graphics, mouseX, mouseY, deltaTracker.getGameTimeDeltaTicks());
        }

        // Hide player overlays
        if (SelfiecamClient.getCameraController().isInSelfieMode()) {
             ci.cancel();

             // keep the chat
             if (!this.minecraft.options.hideGui) {
                 this.extractChat(graphics, deltaTracker);
             }
        }
    }
}
