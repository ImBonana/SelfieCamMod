package me.imbanana.selfiecam.keybinds;

import com.mojang.blaze3d.platform.InputConstants;
import me.imbanana.selfiecam.ModCamera;
import me.imbanana.selfiecam.ModPlayerController;
import me.imbanana.selfiecam.SelfiecamClient;
import me.imbanana.selfiecam.gui.CameraControlsScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.HumanoidArm;

import java.util.function.Consumer;

public class ModKeybinds {
    public static final KeyMapping SELFIE_KEY = new KeyMapping("key.selfiecam.selfie", InputConstants.KEY_F6, KeyMapping.Category.MISC);
    public static final KeyMapping EDIT_KEY = new KeyMapping("key.selfiecam.edit", InputConstants.KEY_LALT, KeyMapping.Category.MISC);

    private static boolean releasedMouse = false;

    public static void registerModKeybinds(Consumer<KeyMapping> registerer) {
        registerer.accept(SELFIE_KEY);
        registerer.accept(EDIT_KEY);
    }

    public static void handleKeyPress() {
        ModCamera cameraController = SelfiecamClient.getCameraController();
        ModPlayerController playerController = SelfiecamClient.getPlayerController();
        CameraControlsScreen cameraGui = SelfiecamClient.getCameraControlsGui();

        if (SELFIE_KEY.consumeClick()) {
            HumanoidArm mainArm = Minecraft.getInstance().options.mainHand().get();
            boolean cameraSelfie = cameraController.isInSelfieMode();
            HumanoidArm selfieHand = playerController.getSelfieArm();

            if (cameraSelfie && selfieHand == mainArm && selfieHand != null) {
                playerController.setSelfieArm(mainArm.getOpposite());
            } else if (cameraSelfie && selfieHand != null) {
                cameraController.setInSelfieMode(false);
                playerController.setSelfieArm(mainArm);
                cameraGui.hideOverlay();
                cameraController.setCameraEffect(null);
            } else {
                cameraController.setInSelfieMode(true);
                playerController.setSelfieArm(mainArm);
                cameraGui.showOverlay();
            }
        }

        boolean isCameraControlsEnabled = cameraGui.isEnabled();

        if (EDIT_KEY.consumeClick() && isCameraControlsEnabled) {
            Minecraft.getInstance().mouseHandler.releaseMouse();
            releasedMouse = true;
        } else if((releasedMouse && !EDIT_KEY.isDown()) || (releasedMouse && !isCameraControlsEnabled)) {
            Minecraft.getInstance().mouseHandler.grabMouse();
            releasedMouse = false;
        }
    }
}
