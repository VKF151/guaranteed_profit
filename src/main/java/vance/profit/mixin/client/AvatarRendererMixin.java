package vance.profit.mixin.client;

import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import vance.profit.Guaranteed_profit;
import vance.profit.components.ModComponents;
import vance.profit.item.ModItems;

@Mixin(targets = "net.minecraft.client.renderer.entity.player.AvatarRenderer")
public class AvatarRendererMixin<AvatarlikeEntity extends Avatar & ClientAvatarEntity> {

    @Inject(method = "getArmPose(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/client/model/HumanoidModel$ArmPose;",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void injectScytheArmPose(Avatar avatar, ItemStack itemInHand, InteractionHand hand, CallbackInfoReturnable<HumanoidModel.ArmPose> cir) {
        if (!itemInHand.isEmpty() && !avatar.isSwinging() && itemInHand.is(ModItems.CRAZY_SCYTHE)) {
            cir.setReturnValue(HumanoidModel.ArmPose.SPEAR);
        }
    }

    @Inject(method = "shouldShowName(Lnet/minecraft/world/entity/Avatar;D)Z", at = @At("HEAD"), cancellable = true)
    protected void dontShowName(AvatarlikeEntity entity, double distanceToCameraSq, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = entity.getItemBySlot(EquipmentSlot.HEAD);
        boolean hidesName = stack.getOrDefault(ModComponents.HIDES_NAME, false);
        if (entity instanceof Player && hidesName) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
    private void isItAFleshWound(AvatarlikeEntity entity, AvatarRenderState state, float partialTicks, CallbackInfo ci) {
        if (entity instanceof Player player && player.getAttachedOrSet(Guaranteed_profit.AMPUTEE, false)) {
            state.setData(Guaranteed_profit.FLESH_WOUND, true);
        }
    }

}
