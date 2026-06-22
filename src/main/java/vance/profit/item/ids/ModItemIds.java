package vance.profit.item.ids;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import vance.profit.Guaranteed_profit;

public class ModItemIds {
    public static final ResourceKey<Item> SLOT_SPINNER = create("slot_spinner");
    public static final ResourceKey<Item> CRAZY_SLOTS = create("crazy_slots");
    public static final ResourceKey<Item> CRAZY_SCYTHE = create("crazy_scythe");
    public static final ResourceKey<Item> CRAZY_MACE = create("crazy_mace");
    public static final ResourceKey<Item> CRAZY_TRIDENT = create("crazy_trident");
    public static final ResourceKey<Item> CRAZY_AXE = create("crazy_axe");
    public static final ResourceKey<Item> CRAZY_SWORD= create("crazy_sword");


    private static ResourceKey<Item> create(final String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, name));
    }
}
