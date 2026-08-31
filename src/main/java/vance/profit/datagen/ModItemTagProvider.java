package vance.profit.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.references.ItemIds;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import org.jetbrains.annotations.NotNull;
import vance.profit.item.HouseArmorMaterial;
import vance.profit.item.ids.ModBlockItemIds;
import vance.profit.item.ids.ModItemIds;
import vance.profit.util.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
    public ModItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider wrapperLookup) {
        builder(ModTags.Items.CRAZY_SLOTS_REPAIR).add(ItemIds.EMERALD);
        builder(ItemTags.AXES).add(ModItemIds.CRAZY_AXE);
        builder(ItemTags.SWORDS).add(ModItemIds.CRAZY_SWORD);
        builder(ItemTags.SWORDS).add(ModItemIds.CRAZY_SCYTHE);
        builder(ItemTags.HOES).add(ModItemIds.CRAZY_SCYTHE);
        builder(ItemTags.MACE_ENCHANTABLE).add(ModItemIds.CRAZY_MACE);
        builder(ItemTags.TRIDENT_ENCHANTABLE).add(ModItemIds.CRAZY_TRIDENT);
        builder(HouseArmorMaterial.REPAIRS_HOUSE_ARMOR).add(ModBlockItemIds.SLOT_MACHINE.item());
        builder(ItemTags.HEAD_ARMOR).add(ModItemIds.HOUSES_HAND_MASK);
    }
}
