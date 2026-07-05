package vance.profit.effect;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import vance.profit.Guaranteed_profit;

public class ModEffects {
    public static final Holder<MobEffect> GAMBLERS_FAVOR = register("gamblers_favor", new GamblersFavorEffect());

    public static final Holder<MobEffect> STIMULATED = register("stimulated", new StimulatedEffect()
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, Identifier.withDefaultNamespace("effect.stimulated"), 0.075F, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    private static Holder<MobEffect> register(final String name, final MobEffect mobEffect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, name), mobEffect);
    }


    public static void initialize() {}
}
