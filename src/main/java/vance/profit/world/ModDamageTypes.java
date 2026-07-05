package vance.profit.world;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import vance.profit.Guaranteed_profit;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> OVERDOSE_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, "overdose"));

    public static void initialize() {}
}
