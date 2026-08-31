package vance.profit.item;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.*;
import vance.profit.Guaranteed_profit;

import java.util.Map;

public class HouseArmorMaterial {
    public static final int BASE_ARMOR_DURABILITY = 37;
    public static final ResourceKey<EquipmentAsset> HOUSE_ARMOR_MATERIAL_ASSET_KEY = ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, "house"));
    public static final TagKey<Item> REPAIRS_HOUSE_ARMOR = TagKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, "repairs_house_armor"));
    public static final ArmorMaterial INSTANCE = new ArmorMaterial(
            BASE_ARMOR_DURABILITY,
            Map.of(
                    ArmorType.HELMET, 3,
                    ArmorType.CHESTPLATE, 8,
                    ArmorType.LEGGINGS, 6,
                    ArmorType.BOOTS, 3
            ),
            15,
            SoundEvents.ARMOR_EQUIP_CHAIN,
            2.0F,
            0.0F,
            REPAIRS_HOUSE_ARMOR,
            HOUSE_ARMOR_MATERIAL_ASSET_KEY
    );
}
