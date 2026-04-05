package me.imbanana.selfiecam.fabric;

import me.imbanana.selfiecam.SelfiecamClient;
import me.imbanana.selfiecam.keybinds.ModKeybinds;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;

public final class SelfiecamFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SelfiecamClient.init();

        ModKeybinds.registerModKeybinds(KeyMappingHelper::registerKeyMapping);
        ClientTickEvents.END_CLIENT_TICK.register(minecraft -> {
            ModKeybinds.handleKeyPress();
        });
    }
}
