package me.imbanana.selfiecam;

import me.imbanana.selfiecam.gui.CameraControlsScreen;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SelfiecamClient {
    public static final String MOD_ID = "selfiecam";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static ModCamera cameraController;
    private static ModPlayerController playerController;
    private static CameraControlsScreen cameraControlsScreen;

    public static void init() {
        // Write common init code here.
        cameraController = new ModCamera();
        playerController = new ModPlayerController();
        cameraControlsScreen = new CameraControlsScreen();


    }

    public static ModCamera getCameraController() {
        return cameraController;
    }

    public static ModPlayerController getPlayerController() {
        return playerController;
    }

    public static CameraControlsScreen getCameraControlsGui() {
        return cameraControlsScreen;
    }

    public static Identifier idOf(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
