package vance.profit.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import org.jetbrains.annotations.NotNull;
import vance.profit.item.ids.ModBlockItemIds;
import vance.profit.util.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
    public ModBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider wrapperLookup) {
        builder(ModTags.Blocks.NEEDS_CRAZY_SLOTS_TOOL).forceAddTag(BlockTags.NEEDS_DIAMOND_TOOL);
        builder(BlockTags.NEEDS_IRON_TOOL).add(ModBlockItemIds.SLOT_MACHINE.block());
    }
}
