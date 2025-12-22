package me.imbanana.selfiecam;

import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ModShaders {
    public static final List<Identifier> SHADERS = new ArrayList<>();

    public static final Identifier CLASSIC = register("classic");
    public static final Identifier COOL = register("cool");
    public static final Identifier GRAYSCALE = register("grayscale");
    public static final Identifier NOSTALGIC = register("nostalgic");
    public static final Identifier RETRO = register("retro");
    public static final Identifier SKY_BLUE = register("sky_blue");
    public static final Identifier VIBRANT = register("vibrant");


    private static Identifier register(String path) {
        Identifier identifier = SelfiecamClient.idOf(path);
        SHADERS.add(identifier);
        return identifier;
    }
}
