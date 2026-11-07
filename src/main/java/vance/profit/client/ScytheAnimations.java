package vance.profit.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.util.Mth;

public class ScytheAnimations {

    public static <T extends HumanoidRenderState> void thirdPersonHandUse(
            final ModelPart arm, final T state
    ) {
        float f = 20;
        float g = Mth.clamp(state.ticksUsingItem, 0.0F, f);
        float h = g / f;
        arm.xRot = Math.clamp(arm.xRot -= ((Mth.lerp(h, 0.4F, 1.5F))), -3.0F, -2.0F);
        arm.yRot = Math.clamp(arm.yRot += Mth.lerp(h, 0.4F, 0.5F) * 0.75F, -0.05F, 0.35F);
        arm.zRot -= (Mth.lerp(h, 0.4F, 1.5F));
    }
    public static void firstPersonUse(
            final PoseStack poseStack, final FirstPersonHandsAndItemsRenderState state
    ) {
        float f = 20;
        float g = Mth.clamp(state.mainHandUseDuration - state.useItemRemainingTicks, 0.0F, f);
        float h = g / f;
        poseStack.translate(0.5 + (h * 0.25), -0.45, -0.65);
        poseStack.rotateAround(
                Axis.ZN.rotationDegrees(Mth.lerp(h, 0F, 67.5F)),
                0.0F,
                0.1F,
                0.0F
        );


    }
}
