package vance.profit.item;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DeathProtection;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.Block;
import vance.profit.Guaranteed_profit;
import vance.profit.block.ModBlocks;
import vance.profit.item.custom.*;
import vance.profit.item.ids.ModBlockItemId;
import vance.profit.item.ids.ModBlockItemIds;
import vance.profit.item.ids.ModItemIds;

import java.util.function.BiFunction;
import java.util.function.Function;

import static net.minecraft.world.item.Items.BREEZE_ROD;

public class ModItems {

    public static final Item SLOT_SPINNER = registerItem(ModItemIds.SLOT_SPINNER,
            new Item.Properties()
                    .stacksTo(7)
                    .fireResistant()
                    .rarity(Rarity.EPIC)
    );

    public static final Item CRAZY_SLOTS = registerItem(ModItemIds.CRAZY_SLOTS, CrazySlotsItem::new,
            new Item.Properties()
                    .stacksTo(1)
                    .fireResistant()
    );

    public static final Item CRAZY_SCYTHE = registerItem(ModItemIds.CRAZY_SCYTHE, CrazyScytheItem::new,
            new Item.Properties()
                    .stacksTo(1)
                    .fireResistant()
                    .rarity(Rarity.RARE)
                    .hoe(ModToolMaterials.CRAZY_SLOTS, 6.0F, -2.9F)
                    .attributes(CrazyScytheItem.createAttributeModifiers())
    );

    public static final Item CRAZY_MACE = registerItem(ModItemIds.CRAZY_MACE, CrazyMaceItem::new,
            new Item.Properties()
                    .rarity(Rarity.RARE)
                    .durability(2031)
                    .component(DataComponents.TOOL, CrazyMaceItem.createToolProperties())
                    .repairable(BREEZE_ROD)
                    .attributes(CrazyMaceItem.createAttributeModifiers())
                    .enchantable(15)
                    .component(DataComponents.WEAPON, new Weapon(1))
                    .fireResistant()
                    .stacksTo(1)
    );

    public static final Item CRAZY_TRIDENT = registerItem(ModItemIds.CRAZY_TRIDENT, CrazyTridentitem::new,
            new Item.Properties()
                    .rarity(Rarity.RARE)
                    .fireResistant()
                    .stacksTo(1)
                    .durability(2031)
                    .attributes(TridentItem.createAttributes())
                    .component(DataComponents.TOOL, TridentItem.createToolProperties())
                    .enchantable(1)
                    .component(DataComponents.WEAPON, new Weapon(1))
    );

    public static final Item CRAZY_AXE = registerItem(ModItemIds.CRAZY_AXE, (p) -> new AxeItem(
            ModToolMaterials.CRAZY_SLOTS, 5.0F, -3.0F, p),
            new Item.Properties()
                    .fireResistant()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
    );

    public static final Item CRAZY_SWORD = registerItem(ModItemIds.CRAZY_SWORD, (
            new Item.Properties()
                    .sword(ModToolMaterials.CRAZY_SLOTS, 3.0F, -2.4F)
                    .fireResistant()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .component(DataComponents.DEATH_PROTECTION, DeathProtection.TOTEM_OF_UNDYING)
    ));

    public static final Item HOUSES_HAND_MASK = registerItem(ModItemIds.HOUSES_HAND_MASK, HousesHandMaskItem::new,
            new Item.Properties()
                    .humanoidArmor(HouseArmorMaterial.INSTANCE, ArmorType.HELMET)
                    .durability(ArmorType.HELMET.getDurability(HouseArmorMaterial.BASE_ARMOR_DURABILITY))
                    .attributes(HousesHandMaskItem.createAttributeModifiers())
    );

    public static final Item SLOT_MACHINE = registerBlock(ModBlockItemIds.SLOT_MACHINE, ModBlocks.SLOT_MACHINE);

    private static Item registerBlock(final ModBlockItemId id, final Block block) {
        return registerBlock(id, block, BlockItem::new);
    }

    private static Item registerBlock(final ModBlockItemId id, final Block block, final BiFunction<Block, Item.Properties, Item> itemFactory) {
        return registerBlock(id, block, itemFactory, new Item.Properties());
    }

    private static Item registerBlock(final ModBlockItemId id, final Block block, final BiFunction<Block, Item.Properties, Item> itemFactory, final Item.Properties properties) {
        return registerItem(id.item(), (p) -> itemFactory.apply(block, p), properties.useBlockDescriptionPrefix().requiredFeatures(block.requiredFeatures()));
    }

    private static Item registerItem(final ResourceKey<Item> id, final Item.Properties properties) {
        return registerItem(id, Item::new, properties);
    }

    private static Item registerItem(final ResourceKey<Item> id, final Function<Item.Properties, Item> itemFactory, final Item.Properties properties) {
        Item item = itemFactory.apply(properties.setId(id));
        if (item instanceof BlockItem blockItem) {
            blockItem.registerBlocks(Item.BY_BLOCK, item);
        }

        return Registry.register(BuiltInRegistries.ITEM, id, item);
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
            fabricItemGroupEntries.accept(ModItems.SLOT_MACHINE);
            fabricItemGroupEntries.accept(ModItems.CRAZY_SLOTS);
            fabricItemGroupEntries.accept(ModItems.CRAZY_SWORD);
            fabricItemGroupEntries.accept(ModItems.CRAZY_SCYTHE);
            fabricItemGroupEntries.accept(ModItems.CRAZY_MACE);
            fabricItemGroupEntries.accept(ModItems.CRAZY_TRIDENT);
            fabricItemGroupEntries.accept(ModItems.CRAZY_AXE);
            fabricItemGroupEntries.accept(ModItems.SLOT_SPINNER);
            fabricItemGroupEntries.accept(ModItems.HOUSES_HAND_MASK);
        });
    }
}
