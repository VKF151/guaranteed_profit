package vance.profit.world;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.RemoveStatusEffectsConsumeEffect;
import vance.profit.effect.ModEffects;

public class ModConsumables {
    public static final Consumable COCA_LEAF = defaultFood()
            .consumeSeconds(0.8F)
            .onConsume(new RemoveStatusEffectsConsumeEffect(MobEffects.WEAKNESS))
            .onConsume(new RemoveStatusEffectsConsumeEffect(MobEffects.MINING_FATIGUE))
            .onConsume(new RemoveStatusEffectsConsumeEffect(MobEffects.HUNGER))
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(ModEffects.STIMULATED, 100, 0)))
            .build();

    public static Consumable.Builder defaultFood() {
        return Consumable.builder().consumeSeconds(1.6F).animation(ItemUseAnimation.EAT).sound(SoundEvents.GENERIC_EAT).hasConsumeParticles(true);
    }

    public static Consumable.Builder defaultDrink() {
        return Consumable.builder().consumeSeconds(1.6F).animation(ItemUseAnimation.DRINK).sound(SoundEvents.GENERIC_DRINK).hasConsumeParticles(false);
    }
}
