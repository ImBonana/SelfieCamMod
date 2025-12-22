package me.imbanana.selfiecam.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.imbanana.selfiecam.SelfiecamClient;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin extends Avatar implements ContainerUser {

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @ModifyReturnValue(
            method = "isScoping",
            at = @At("RETURN")
    )
    private boolean checkSelfieCam(boolean original) {
        return original && !SelfiecamClient.getCameraController().isInSelfieMode();
    }
}
