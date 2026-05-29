package vance.profit.item.custom;

import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class CrazyTridentitem extends TridentItem {
    public CrazyTridentitem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public void postHurtEnemy(ItemStack stack, @NonNull LivingEntity target, @NonNull LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level world, Player user, @NonNull InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (itemStack.nextDamageWillBreak()) {
            return InteractionResult.FAIL;
        } else if (!user.isInWaterOrRain()) {
            return InteractionResult.FAIL;
        } else {
            user.startUsingItem(hand);
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public boolean releaseUsing(@NonNull ItemStack stack, @NonNull Level world, @NonNull LivingEntity user, int remainingUseTicks) {
        if (user instanceof Player playerEntity) {
            int i = this.getUseDuration(stack, user) - remainingUseTicks;
            if (i < 10) {
                return false;
            } else {
                if (!playerEntity.isInWaterOrRain()) {
                    return false;
                } else if (stack.nextDamageWillBreak()) {
                    return false;
                } else {
                    Holder<SoundEvent> registryEntry = EnchantmentHelper.pickHighestLevel(stack, EnchantmentEffectComponents.TRIDENT_SOUND)
                            .orElse(SoundEvents.TRIDENT_RIPTIDE_2);
                    playerEntity.awardStat(Stats.ITEM_USED.get(this));

                    float g = playerEntity.getYRot();
                    float h = playerEntity.getXRot();
                    float j = -Mth.sin(g * (float) (Math.PI / 180.0)) * Mth.cos(h * (float) (Math.PI / 180.0));
                    float k = -Mth.sin(h * (float) (Math.PI / 180.0));
                    float l = Mth.cos(g * (float) (Math.PI / 180.0)) * Mth.cos(h * (float) (Math.PI / 180.0));
                    float m = Mth.sqrt(j * j + k * k + l * l);
                    j *= 3 / m;
                    k *= 3 / m;
                    l *= 3 / m;
                    playerEntity.push(j, k, l);
                    playerEntity.startAutoSpinAttack(20, 8.0F, stack);
                    if (playerEntity.onGround()) {
                        playerEntity.move(MoverType.SELF, new Vec3(0.0, 1.1999999F, 0.0));
                    }

                    world.playSound(null, playerEntity, registryEntry.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
                    return true;

                }
            }
        } else {
            return false;
        }
    }
}
