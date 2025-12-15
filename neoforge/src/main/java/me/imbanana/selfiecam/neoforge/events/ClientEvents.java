package me.imbanana.selfiecam.neoforge.events;

import me.imbanana.selfiecam.SelfiecamClient;
import me.imbanana.selfiecam.keybinds.ModKeybinds;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = SelfiecamClient.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void registerKeybinds(RegisterKeyMappingsEvent event) {
        ModKeybinds.registerModKeybinds(event::register);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        ModKeybinds.handleKeyPress();
    }
}
