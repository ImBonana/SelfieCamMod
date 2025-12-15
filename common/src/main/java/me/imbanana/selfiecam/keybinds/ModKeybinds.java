package me.imbanana.selfiecam.keybinds;

import me.imbanana.selfiecam.SelfiecamClient;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.HumanoidArm;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class ModKeybinds {
    public static final KeyMapping SELFIE_KEY = new KeyMapping("key.selfiecam.selfie", GLFW.GLFW_KEY_F6, KeyMapping.Category.MISC);
    public static final KeyMapping EDIT_KEY = new KeyMapping("key.selfiecam.edit", GLFW.GLFW_KEY_LEFT_ALT, KeyMapping.Category.MISC);

    private static boolean releasedMouse = false;

    public static void registerModKeybinds(Consumer<KeyMapping> registerer) {
        registerer.accept(SELFIE_KEY);
        registerer.accept(EDIT_KEY);
    }

    public static void handleKeyPress() {
        if (SELFIE_KEY.consumeClick()) {
            HumanoidArm mainArm = Minecraft.getInstance().options.mainHand().get();
            boolean cameraSelfie = SelfiecamClient.getCameraController().isInSelfieMode();
            HumanoidArm selfieHand = SelfiecamClient.getPlayerController().getSelfieArm();

            if (cameraSelfie && selfieHand == mainArm && selfieHand != null) {
                SelfiecamClient.getPlayerController().setSelfieArm(mainArm.getOpposite());
            } else if (cameraSelfie && selfieHand != null) {
                SelfiecamClient.getCameraController().setInSelfieMode(false);
                SelfiecamClient.getPlayerController().setSelfieArm(mainArm);
                SelfiecamClient.getCameraControlsGui().hideOverlay();
                Minecraft.getInstance().gameRenderer.clearPostEffect();
            } else {
                SelfiecamClient.getCameraController().setInSelfieMode(true);
                SelfiecamClient.getPlayerController().setSelfieArm(mainArm);
                SelfiecamClient.getCameraControlsGui().showOverlay();
            }
        }

        boolean isCameraControlsEnabled = SelfiecamClient.getCameraControlsGui().isEnabled();

        if (EDIT_KEY.consumeClick() && isCameraControlsEnabled) {
            Minecraft.getInstance().mouseHandler.releaseMouse();
            releasedMouse = true;
        } else if((releasedMouse && !EDIT_KEY.isDown()) || (releasedMouse && !isCameraControlsEnabled)) {
            Minecraft.getInstance().mouseHandler.grabMouse();
            releasedMouse = false;
        }
    }
}
