package vance.profit.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import org.jetbrains.annotations.NotNull;
import vance.profit.item.ModItems;
import vance.profit.util.ModTags;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ModItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.@NotNull WrapperLookup wrapperLookup) {
        valueLookupBuilder(ModTags.Items.CRAZY_SLOTS_REPAIR)
                .add(Items.EMERALD);

        valueLookupBuilder(ItemTags.AXES)
                .add(ModItems.CRAZY_AXE);

        valueLookupBuilder(ItemTags.SWORDS)
                .add(ModItems.CRAZY_SWORD);

        valueLookupBuilder(ItemTags.SWORDS)
                .add(ModItems.CRAZY_SCYTHE);

        valueLookupBuilder(ItemTags.HOES)
                .add(ModItems.CRAZY_SCYTHE);

        valueLookupBuilder(ItemTags.MACE_ENCHANTABLE)
                .add(ModItems.CRAZY_MACE);

        valueLookupBuilder(ItemTags.TRIDENT_ENCHANTABLE)
                .add(ModItems.CRAZY_TRIDENT);
    }
}
