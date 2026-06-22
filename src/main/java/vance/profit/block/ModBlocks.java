package vance.profit.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import vance.profit.block.custom.SlotMachineBlock;
import vance.profit.item.ids.ModBlockItemId;
import vance.profit.item.ids.ModBlockItemIds;

import java.util.function.Function;

public class ModBlocks {

    public static final Block SLOT_MACHINE = register(ModBlockItemIds.SLOT_MACHINE, SlotMachineBlock::new, BlockBehaviour.Properties.of().sound(SoundType.HEAVY_CORE).requiresCorrectToolForDrops().strength(2.0F, 6.0F));

    private static Block register(final ModBlockItemId id, final Function<BlockBehaviour.Properties, Block> factory, final BlockBehaviour.Properties properties) {
        return register(id.block(), factory, properties);
    }

    public static Block register(final ResourceKey<Block> id, final Function<BlockBehaviour.Properties, Block> factory, final BlockBehaviour.Properties properties) {
        Block block = factory.apply(properties.setId(id));
        return Registry.register(BuiltInRegistries.BLOCK, id, block);
    }

    public  static void initialize() {}
}
