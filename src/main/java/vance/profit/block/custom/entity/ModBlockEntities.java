package vance.profit.block.custom.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import vance.profit.Guaranteed_profit;
import vance.profit.block.ModBlocks;

public class ModBlockEntities {
    public static <T extends BlockEntityType<?>> T register(String path, T blockEntityType) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, path), blockEntityType);
    }

    public static final BlockEntityType<SlotMachineBlockEntity> SLOT_MACHINE_ENTITY = register("slot_machine_entity",
            FabricBlockEntityTypeBuilder.create(SlotMachineBlockEntity::new, ModBlocks.SLOT_MACHINE).build());

    public static void initialize() {}

}
