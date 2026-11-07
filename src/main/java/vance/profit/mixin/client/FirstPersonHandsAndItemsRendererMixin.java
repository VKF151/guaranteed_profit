package vance.profit.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vance.profit.client.ScytheAnimations;
import vance.profit.item.ModItems;

@Mixin(FirstPersonHandsAndItemsRenderer.class)
public class FirstPersonHandsAndItemsRendererMixin {
    @Inject(method = "submitArmWithItem", at = @At("HEAD"), cancellable = true)
    private void submitModItemPose(PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState state, float partialTicks, float xRot, InteractionHand hand, float attack, ItemStack itemStack, float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords, CallbackInfo ci) {
        if (!state.isScoping) {
            AvatarRenderState avatarRenderState = playerState.avatarRenderState;
            if (avatarRenderState != null && state.useItemRemainingTicks > 0 && itemStack.is(ModItems.CRAZY_SCYTHE)) {
                poseStack.pushPose();
                ScytheAnimations.firstPersonUse(poseStack, state);
                state.mainHandRenderState.submit(poseStack, submitNodeCollector, lightCoords, OverlayTexture.NO_OVERLAY, 0);
                poseStack.popPose();
                ci.cancel();
            }
        }
    }
}
