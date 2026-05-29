package vance.profit.item;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.item.component.DeathProtection;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Rarity;
import vance.profit.Guaranteed_profit;
import vance.profit.block.ModBlocks;
import vance.profit.item.custom.*;

public class ModItems {

    public static final Item SLOT_SPINNER = registerItem("slot_spinner", new Item(
            new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, "slot_spinner")))
                    .stacksTo(7)
                    .fireResistant()
                    .rarity(Rarity.EPIC)
    ));

    public static final Item CRAZY_SLOTS = registerItem("crazy_slots", new CrazySlotsItem(
            new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, "crazy_slots")))
                    .stacksTo(1)
                    .fireResistant()
    ));

    public static final Item CRAZY_SCYTHE = registerItem("crazy_scythe", new CrazyScytheItem(
            new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, "crazy_scythe")))
                    .stacksTo(1)
                    .fireResistant()
                    .rarity(Rarity.RARE)
                    .hoe(ModToolMaterials.CRAZY_SLOTS, 6.0F, -2.9F)
                    .attributes(CrazyScytheItem.createAttributeModifiers())
    ));

    public static final Item CRAZY_MACE = registerItem("crazy_mace", new CrazyMaceItem(
            new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, "crazy_mace")))
                    .stacksTo(1)
                    .fireResistant()
                    .durability(2031)
                    .rarity(Rarity.RARE)
                    .attributes(CrazyMaceItem.createAttributeModifiers())
    ));

    public static final Item CRAZY_TRIDENT = registerItem("crazy_trident", new CrazyTridentitem(
            new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, "crazy_trident")))
                    .fireResistant()
                    .stacksTo(1)
                    .durability(2031)
                    .rarity(Rarity.RARE)
                    .attributes(TridentItem.createAttributes())
    ));

    public static final Item CRAZY_AXE = registerItem("crazy_axe", new CrazyAxeItem(
            new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, "crazy_axe")))
                    .fireResistant()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .axe(ModToolMaterials.CRAZY_SLOTS, 5.0F, -3.0F)
    ));

    public static final Item CRAZY_SWORD = registerItem("crazy_sword", new CrazySwordItem(
            new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, "crazy_sword")))
                    .fireResistant()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .component(DataComponents.DEATH_PROTECTION, DeathProtection.TOTEM_OF_UNDYING)
                    .sword(ModToolMaterials.CRAZY_SLOTS, 3.0F, -2.4F)
    ));

    private static Item registerItem(String name, Item item) {
        return Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, name), item);
    }

    public static final ResourceKey<CreativeModeTab> GUARANTEED_PROFIT_GROUP_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, "guaranteed_profit_group"));
    public static final CreativeModeTab GUARANTEED_PROFIT_GROUP = FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(ModItems.CRAZY_SLOTS))
            .title(Component.translatable("itemGroup.guaranteed_profit"))
            .build();

    public static void registerModItems() {
        Guaranteed_profit.LOGGER.info("Registering Mod Items for " + Guaranteed_profit.MOD_ID);

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, GUARANTEED_PROFIT_GROUP_KEY, GUARANTEED_PROFIT_GROUP);

        CreativeModeTabEvents.modifyOutputEvent(GUARANTEED_PROFIT_GROUP_KEY).register(fabricItemGroupEntries -> {
            fabricItemGroupEntries.accept(ModBlocks.SLOT_MACHINE.asItem());
            fabricItemGroupEntries.accept(ModItems.CRAZY_SLOTS);
            fabricItemGroupEntries.accept(ModItems.CRAZY_SWORD);
            fabricItemGroupEntries.accept(ModItems.CRAZY_SCYTHE);
            fabricItemGroupEntries.accept(ModItems.CRAZY_MACE);
            fabricItemGroupEntries.accept(ModItems.CRAZY_TRIDENT);
            fabricItemGroupEntries.accept(ModItems.CRAZY_AXE);
            fabricItemGroupEntries.accept(ModItems.SLOT_SPINNER);
        });
    }
}
