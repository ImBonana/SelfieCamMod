package me.imbanana.selfiecam;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Function6;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HeadedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ModPlayerController {
    private HumanoidArm selfieArm = null;

    public void setSelfieArm(HumanoidArm selfieArm) {
        this.selfieArm = selfieArm;
    }

    public HumanoidArm getSelfieArm() {
        return selfieArm;
    }

    public <S extends ArmedEntityRenderState, M extends EntityModel<S> & ArmedModel<S> & HeadedModel> boolean renderSelfieStick(
            S avatarRenderState,
            PoseStack poseStack,
            ItemStackRenderState itemStackRenderState,
            HumanoidArm humanoidArm,
            SubmitNodeCollector submitNodeCollector,
            int i,
            M parentModel
    ) {
        if (humanoidArm != this.selfieArm) return false;

        LocalPlayer player = Minecraft.getInstance().player;
        ItemModelResolver itemModelResolver = Minecraft.getInstance().getItemModelResolver();
        ItemDisplayContext itemDisplayContext = this.selfieArm == HumanoidArm.RIGHT ? ItemDisplayContext.THIRD_PERSON_RIGHT_HAND : ItemDisplayContext.THIRD_PERSON_LEFT_HAND;

        poseStack.pushPose();

        itemStackRenderState.clear();
        itemModelResolver.updateForTopItem(
                itemStackRenderState,
                new ItemStack(Items.STICK, 1),
                itemDisplayContext,
                player.level(),
                player,
                player.getId() + itemDisplayContext.ordinal()

        );

        parentModel.translateToHand(avatarRenderState, humanoidArm, poseStack);
        poseStack.mulPose(Axis.XP.rotationDegrees(-20.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        poseStack.translate((humanoidArm == HumanoidArm.LEFT ? -1 : 1) / 16.0F, 0.8F, -0.35);
        itemStackRenderState.submit(poseStack, submitNodeCollector, i, OverlayTexture.NO_OVERLAY, avatarRenderState.outlineColor);

        poseStack.mulPose(Axis.YP.rotationDegrees(30f));
        poseStack.popPose();

        return true;
    }

    public void setupPlayerHand(ModelPart rightArm, ModelPart leftArm, ModelPart head) {
        head.xRot = (float) Math.clamp(head.xRot, Math.toRadians(ModCamera.MIN_CAMERA_ANGLE), Math.toRadians(ModCamera.MAX_CAMERA_ANGLE));

        if (selfieArm == HumanoidArm.RIGHT) {
            rightArm.xRot = (float) (head.xRot - Math.PI / 2 + head.xRot * -0.25f);
            rightArm.yRot = head.yRot;
            rightArm.zRot = head.xRot * 0.15f;
        } else if (selfieArm == HumanoidArm.LEFT) {
            leftArm.xRot = (float) (head.xRot - Math.PI / 2 + head.xRot * -0.25f);
            leftArm.yRot = head.yRot;
            leftArm.zRot = head.xRot * -0.15f;
        }
    }
}
