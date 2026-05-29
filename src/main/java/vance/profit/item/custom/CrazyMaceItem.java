package vance.profit.item.custom;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

import java.util.Objects;
import java.util.function.Predicate;

public class CrazyMaceItem extends MaceItem {
    public CrazyMaceItem(Properties settings) {
        super(settings);
    }

    public static final Identifier BASE_ATTACK_REACH_MODIFIER_ID = Identifier.withDefaultNamespace("base_attack_reach");

    public static ItemAttributeModifiers createAttributeModifiers() {
        return ItemAttributeModifiers.builder()
                .add(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 5.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, -3.25F, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .add(
                        Attributes.ENTITY_INTERACTION_RANGE,
                        new AttributeModifier(BASE_ATTACK_REACH_MODIFIER_ID, 0.25, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND
                )
                .build();
    }

    @Override
    public void hurtEnemy(@NonNull ItemStack stack, @NonNull LivingEntity target, @NonNull LivingEntity attacker) {
        if (canSmashAttack(attacker)) {
            ServerLevel serverWorld = (ServerLevel)attacker.level();
            attacker.setDeltaMovement(attacker.getDeltaMovement().with(Direction.Axis.Y, 0.009999999776482582));
            ServerPlayer serverPlayerEntity = (ServerPlayer)attacker ;
            if (attacker instanceof ServerPlayer) {
                serverPlayerEntity.currentImpulseImpactPos = this.getCurrentExplosionImpactPos(serverPlayerEntity);
                serverPlayerEntity.setIgnoreFallDamageFromCurrentImpulse(true, this.calculateImpactPosition(attacker));
                serverPlayerEntity.connection.send(new ClientboundSetEntityMotionPacket(serverPlayerEntity));
            }

            if (target.onGround()) {
                if (attacker instanceof ServerPlayer) {
                    serverPlayerEntity.setSpawnExtraParticlesOnFall(true);
                }

                SoundEvent soundEvent = attacker.fallDistance > 5.0 ? SoundEvents.MACE_SMASH_GROUND_HEAVY : SoundEvents.MACE_SMASH_GROUND;
                serverWorld.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), soundEvent, attacker.getSoundSource(), 1.0F, 1.0F);
            } else {
                serverWorld.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.MACE_SMASH_AIR, attacker.getSoundSource(), 1.0F, 1.0F);
            }

            knockbackNearbyEntities(serverWorld, attacker, target);
        }

    }
    private Vec3 calculateImpactPosition(final LivingEntity attacker) {
        return attacker.isIgnoringFallDamageFromCurrentImpulse() && Objects.requireNonNull(attacker.currentImpulseImpactPos).y <= attacker.position().y ? attacker.currentImpulseImpactPos : attacker.position();
    }

    private static void knockbackNearbyEntities(Level world, Entity attacker, Entity attacked) {
        world.levelEvent(2013, attacked.getOnPos(), 750);
        world.getEntitiesOfClass(LivingEntity.class, attacked.getBoundingBox().inflate(3.5), getKnockbackPredicate(attacker, attacked)).forEach((entity) -> {
            Vec3 vec3d = entity.position().subtract(attacked.position());
            double d = getKnockback(attacker, entity, vec3d);
            Vec3 vec3d2 = vec3d.normalize().scale(d);
            if (d > 0.0) {
                entity.push(vec3d2.x, 0.699999988079071, vec3d2.z);
                if (entity instanceof ServerPlayer serverPlayerEntity) {
                    serverPlayerEntity.connection.send(new ClientboundSetEntityMotionPacket(serverPlayerEntity));
                }
            }

        });
    }
    private static Predicate<LivingEntity> getKnockbackPredicate(Entity attacker, Entity attacked) {
        return (entity) -> {
            boolean bl;
            boolean bl2;
            boolean bl3;
            boolean var10000;
            label64: {
                bl = !entity.isSpectator();
                bl2 = entity != attacker && entity != attacked;
                bl3 = !attacker.isAlliedTo(entity);
                if (entity instanceof TamableAnimal tameableEntity) {
                    if (attacked instanceof LivingEntity livingEntity) {
                        if (tameableEntity.isTame() && tameableEntity.isOwnedBy(livingEntity)) {
                            var10000 = true;
                            break label64;
                        }
                    }
                }

                var10000 = false;
            }

            boolean bl4;
            label56: {
                bl4 = !var10000;
                if (entity instanceof ArmorStand armorStandEntity) {
                    if (armorStandEntity.isMarker()) {
                        break label56;
                    }
                }

                var10000 = true;
            }

            boolean bl5 = var10000;
            boolean bl6 = attacked.distanceToSqr(entity) <= Math.pow(3.5, 2.0);
            return bl && bl2 && bl3 && bl4 && bl5 && bl6;
        };
    }
    private static double getKnockback(Entity attacker, LivingEntity attacked, Vec3 distance) {
        return (3.5 - distance.length()) * 0.699999988079071 * (double)(attacker.fallDistance > 5.0 ? 2 : 1) * (1.0 - attacked.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
    }
    private Vec3 getCurrentExplosionImpactPos(ServerPlayer player) {
        return player.isIgnoringFallDamageFromCurrentImpulse() && player.currentImpulseImpactPos != null && player.currentImpulseImpactPos.y <= player.position().y ? player.currentImpulseImpactPos : player.position();
    }
}
