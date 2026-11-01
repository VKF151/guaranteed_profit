package vance.profit.resources.ids;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import vance.profit.Guaranteed_profit;

public class ModEntityTypeIds {
    public static final ResourceKey<EntityType<?>> ROULETTE_TABLE = create("roulette_table");

    private static ResourceKey<EntityType<?>> create(final String name) {
        return ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, name));
    }
}
