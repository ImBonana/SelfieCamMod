package me.imbanana.selfiecam.mixin;

import me.imbanana.selfiecam.accessors.IAvatarRenderState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public abstract class AvatarRenderStateMixin extends HumanoidRenderState implements IAvatarRenderState {
    @Unique
    private boolean selfieCam$isCameraEntity = false;

    @Override
    public void selfieCam$setCameraEntity(boolean cameraEntity) {
        this.selfieCam$isCameraEntity = cameraEntity;
    }

    @Override
    public boolean selfieCam$isCameraEntity() {
        return this.selfieCam$isCameraEntity;
    }
}
