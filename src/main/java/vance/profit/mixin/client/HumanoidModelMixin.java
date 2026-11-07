package vance.profit.mixin.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vance.profit.client.ScytheAnimations;
import vance.profit.item.ModItems;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin<T extends HumanoidRenderState>{
    @Shadow
    @Final
    public ModelPart rightArm;

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;)V", at = @At("TAIL"))
    public void modItemPoser(T state, CallbackInfo ci){
        if (state.isUsingItem && state.getUseItemStackForArm(state.mainArm).is(ModItems.CRAZY_SCYTHE)) {
            boolean usingRightHand = state.mainArm == HumanoidArm.RIGHT;
            if (usingRightHand) {
                ScytheAnimations.thirdPersonHandUse(rightArm, state);
            }
        }
    }
}
