package vance.profit.effect;

import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jspecify.annotations.NonNull;
import vance.profit.world.ModDamageTypes;

public class StimulatedEffect extends MobEffect {
    protected StimulatedEffect() {
        super(MobEffectCategory.NEUTRAL, 0xeeffab);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(final int tickCount, final int amplification) {
        int interval = 20;
        return tickCount % interval == 0;
    }

    @Override
    public boolean applyEffectTick(@NonNull ServerLevel level, @NonNull LivingEntity entity, int amplifier) {
        if (entity instanceof Player) {
            ((Player) entity).getFoodData().eat(1, 0.0075F * (amplifier + 1));
        }
        DamageSource overdoseSource = new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE)
                .get(ModDamageTypes.OVERDOSE_DAMAGE.identifier()).orElseThrow());
        if (amplifier >= 6 && entity instanceof LivingEntity) {
            entity.hurtServer(level, overdoseSource, amplifier - 4);
        }
        if (amplifier >= 5 && entity instanceof LivingEntity) {
            entity.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 200, amplifier - 5));
        }

        return super.applyEffectTick(level, entity, amplifier);
    }
}
