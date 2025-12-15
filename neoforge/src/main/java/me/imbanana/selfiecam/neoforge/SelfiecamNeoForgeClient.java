package me.imbanana.selfiecam.neoforge;

import me.imbanana.selfiecam.SelfiecamClient;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.Mod;

@Mod(value = SelfiecamClient.MOD_ID, dist = Dist.CLIENT)
public final class SelfiecamNeoForgeClient {
    public SelfiecamNeoForgeClient() {
        SelfiecamClient.init();
    }
}
