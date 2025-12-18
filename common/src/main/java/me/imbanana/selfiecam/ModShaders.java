package me.imbanana.selfiecam;

import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ModShaders {
    public static final List<ResourceLocation> SHADERS = new ArrayList<>();

    public static final ResourceLocation CLASSIC = register("classic");
    public static final ResourceLocation COOL = register("cool");
    public static final ResourceLocation GRAYSCALE = register("grayscale");
    public static final ResourceLocation NOSTALGIC = register("nostalgic");
    public static final ResourceLocation RETRO = register("retro");
    public static final ResourceLocation SKY_BLUE = register("sky_blue");
    public static final ResourceLocation VIBRANT = register("vibrant");


    private static ResourceLocation register(String path) {
        ResourceLocation resourceLocation = SelfiecamClient.idOf(path);
        SHADERS.add(resourceLocation);
        return resourceLocation;
    }
}
