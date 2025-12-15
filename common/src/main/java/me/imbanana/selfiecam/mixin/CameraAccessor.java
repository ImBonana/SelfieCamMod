package me.imbanana.selfiecam.mixin;

import net.minecraft.client.Camera;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Camera.class)
public interface CameraAccessor {
    @Invoker("setRotation")
    void selfieCam$setRotation(float f, float g);

    @Invoker("move")
    void selfieCam$move(float f, float g, float h);

    @Invoker("setPosition")
    void selfieCam$setPosition(double d, double e, double f);

    @Invoker("setPosition")
    void selfieCam$setPosition(Vec3 pos);

    @Invoker("getMaxZoom")
    float selfieCam$getMaxZoom(float f);

    @Accessor("yRot")
    float selfieCam$getYRot();

    @Accessor("xRot")
    float selfieCam$getXRot();

    @Accessor("position")
    Vec3 selfieCam$getPosition();
}
