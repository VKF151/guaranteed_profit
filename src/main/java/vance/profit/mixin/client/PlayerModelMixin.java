package vance.profit.mixin.client;

import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vance.profit.Guaranteed_profit;

@Mixin(PlayerModel.class)
public class PlayerModelMixin {
    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V", at = @At("TAIL"))
    private void merelyAFleshWound(AvatarRenderState state, CallbackInfo ci) {
        if (Boolean.TRUE.equals(state.getData(Guaranteed_profit.FLESH_WOUND))) {
            PlayerModel instance = (PlayerModel) (Object) this;
            if (state.mainArm == HumanoidArm.RIGHT) {
                instance.leftArm.visible = false;
            } else {
                instance.rightArm.visible = false;
            }
        }
    }
}
