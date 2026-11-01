package vance.profit.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import vance.profit.entity.decoration.RouletteTable;
import vance.profit.resources.ids.ModEntityTypeIds;

public class ModEntityTypes {

    public static final EntityType<RouletteTable> ROULETTE_TABLE = register(
            ModEntityTypeIds.ROULETTE_TABLE,
            EntityType.Builder.of(RouletteTable::new, MobCategory.MISC)
                    .noLootTable()
                    .sized(1.0F, 0.25F)
                    .clientTrackingRange(10)
                    .updateInterval(Integer.MAX_VALUE)
                    .dontTrackDeltas()
    );

    private static <T extends Entity> EntityType<T> register(final ResourceKey<EntityType<?>> id, final EntityType.Builder<T> builder) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, builder.build(id));
    }

    public static void initialize() {}
}
