package vance.profit.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.resources.Identifier;
import vance.profit.Guaranteed_profit;
import vance.profit.block.custom.SlotMachineBlock;

public class ModBlocks {
    public static Block register(Block block, String name, boolean shouldRegisterItem) {
        Identifier id = Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID,  name);

        if (shouldRegisterItem) {
            BlockItem blockItem = new BlockItem(block, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, name))));
            Registry.register(BuiltInRegistries.ITEM, id, blockItem);
        }

        return Registry.register(BuiltInRegistries.BLOCK, id, block);
    }

    public static final Block SLOT_MACHINE = register(
            new SlotMachineBlock(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, "slot_machine"))).sound(SoundType.HEAVY_CORE).requiresCorrectToolForDrops().strength(2.0F, 6.0F)),
            "slot_machine",
            true
    );

    public  static void initialize() {}
}
