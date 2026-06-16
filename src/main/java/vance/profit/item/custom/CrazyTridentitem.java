package vance.profit.item.custom;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import vance.profit.components.ModComponents;

import java.util.ArrayList;
import java.util.List;

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
            user.startUsingItem(hand);
            return InteractionResult.CONSUME;
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
                /*if (!playerEntity.isInWaterOrRain()) {
                    return false;
                } else*/ if (stack.nextDamageWillBreak()) {
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
                    playerEntity.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 20, 1));
                    if (playerEntity.onGround()) {
                        playerEntity.move(MoverType.SELF, new Vec3(0.0, 1.1999999F, 0.0));
                    }


                    ItemStack mainHandItem = playerEntity.getActiveItem();
                    ItemStack originalItem = mainHandItem.get(ModComponents.ORIGINALITEM);
                    Integer weaponId = mainHandItem.get(ModComponents.WEAPON_ID);
                    ItemEnchantments weaponEnchants = mainHandItem.getEnchantments();


                    if (mainHandItem.get(ModComponents.TRANSFORMABLE) != null && !playerEntity.isSpectator()) {
                            List<ItemEnchantments> enchantsList = new ArrayList<>();
                            List<ItemEnchantments> existing = mainHandItem.get(ModComponents.TRANSFORMABLE_ENCHANTS);
                            if (existing != null) {
                                enchantsList.addAll(existing);
                            }

                            if (weaponId != null) {
                                if (weaponId >= 0 && weaponId <= enchantsList.size()) {
                                    enchantsList.set(weaponId -1, weaponEnchants);
                                }
                            }
                            assert originalItem != null;
                            playerEntity.setItemInHand(playerEntity.getUsedItemHand(), originalItem);
                            originalItem.set(ModComponents.TRANSFORMABLE_ENCHANTS, enchantsList);

                            world.playSound(null, playerEntity.blockPosition(),
                                    SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.PLAYERS,
                                    0.35f, 1.0f);

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
