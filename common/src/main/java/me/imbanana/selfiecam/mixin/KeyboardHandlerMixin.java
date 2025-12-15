package me.imbanana.selfiecam.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.platform.InputConstants;
import me.imbanana.selfiecam.SelfiecamClient;
import me.imbanana.selfiecam.gui.CameraControlsScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Definition(id = "i", local = @Local(type = int.class, argsOnly = true))
    @Expression("i != 0")
    @ModifyExpressionValue(
            method = "keyPress",
            at = @At(
                    value = "MIXINEXTRAS:EXPRESSION"
            )
    )
    private boolean injectKeyCheck(boolean original, @Local(argsOnly = true) int action, @Local(argsOnly = true) KeyEvent keyEvent) {
        CameraControlsScreen screen = SelfiecamClient.getCameraControlsGui();

        if (!screen.isEnabled()) return original;
        if (!screen.canInteract()) return original;

        if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT) {
            if (screen.keyPressed(keyEvent) && !screen.isEnabled()) {
                InputConstants.Key key = InputConstants.getKey(keyEvent);
                KeyMapping.set(key, false);
            }
        } else if (action == GLFW.GLFW_RELEASE) {
            screen.keyReleased(keyEvent);
        }

        return original;
    }

    @Inject(
            method = "charTyped",
            at = @At("HEAD")
    )
    private void injectCharTyped(long l, CharacterEvent characterEvent, CallbackInfo ci) {
        if (Minecraft.getInstance().getWindow().handle() != l) return;

        CameraControlsScreen screen = SelfiecamClient.getCameraControlsGui();
        if (!screen.isEnabled()) return;
        if (!screen.canInteract()) return;

        screen.charTyped(characterEvent);
    }
}
