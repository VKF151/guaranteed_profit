package vance.profit.item.custom;


import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.component.SwingAnimation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.NonNull;
import vance.profit.Guaranteed_profit;
import vance.profit.components.ModComponents;
import vance.profit.world.SavedGamblingData;

import java.util.List;

public class CrazyScytheItem extends Item {
    public CrazyScytheItem(Properties settings) {
        super(settings);
    }

    public static final Identifier BASE_ATTACK_REACH_MODIFIER_ID = Identifier.withDefaultNamespace("base_attack_reach");

    public static ItemAttributeModifiers createAttributeModifiers() {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 10.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.9F, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ENTITY_INTERACTION_RANGE,
                        new AttributeModifier(BASE_ATTACK_REACH_MODIFIER_ID, 1.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }

    @Override
    public int getUseDuration(@NonNull ItemStack itemStack, @NonNull LivingEntity user) {
        return 72000;
    }

    @Override
    public boolean releaseUsing(@NonNull ItemStack itemStack, @NonNull Level level, @NonNull LivingEntity entity, int remainingTime) {
        int timeHeld = this.getUseDuration(itemStack, entity) - remainingTime;
        if (entity instanceof Player player && timeHeld >= 20 && level instanceof ServerLevel serverLevel) {
            SavedGamblingData savedGamblingData = SavedGamblingData.getSavedGamblingData(level.getServer());
            savedGamblingData.incrementHousesHoard(-1);
            double damage = player.getAttributeValue(Attributes.ATTACK_DAMAGE);
            DamageSource damageSource = entity.damageSources().playerAttack(player);
            AABB aabb = player.getBoundingBox();
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, aabb.inflate(6.0F, 1.0F, 6.0F));
                for (LivingEntity targetEntity : entities) {
                    if (targetEntity instanceof LivingEntity && targetEntity != player) {
                        targetEntity.hurtServer(serverLevel, damageSource, (float) damage);
                        targetEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 3, 0, false, false));
                        targetEntity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 3, 1, false, false));
                    }
                }
            serverLevel.sendParticles(Guaranteed_profit.FULL_SWEEP_PARTICLE, player.getX(), player.getY((double)0.75F), player.getZ(), 0, 0, (double)0.0F, 0, (double)0.0F);
            player.causeFoodExhaustion(5.0F);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0f, 1.0f);
            player.swing(player.getUsedItemHand(), SwingAnimation.DEFAULT, true);
            return true;

        }
        return false;
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level level, @NonNull Player player, @NonNull InteractionHand hand) {
        if (player.getFoodData().getFoodLevel() >= 10.0F && player.getItemBySlot(EquipmentSlot.HEAD).getOrDefault(ModComponents.HOARD_AMOUNT, 0) > 0) {
            player.startUsingItem(hand);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void onUseTick(@NonNull Level level, @NonNull LivingEntity livingEntity, @NonNull ItemStack itemStack, int ticksRemaining) {
        int timeHeld = this.getUseDuration(itemStack, livingEntity) - ticksRemaining;
        if (timeHeld == 1) {
            level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SoundEvents.SPEAR_USE, SoundSource.PLAYERS, 0.5F, 0.4F);
        }
        super.onUseTick(level, livingEntity, itemStack, ticksRemaining);
    }

    @Override
    public boolean useOnRelease(final @NonNull ItemStack itemStack) {
        return itemStack.is(this);
    }
}
