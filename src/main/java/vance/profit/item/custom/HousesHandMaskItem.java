package vance.profit.item.custom;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.jspecify.annotations.Nullable;
import vance.profit.components.ModComponents;
import vance.profit.world.SavedGamblingData;

public class HousesHandMaskItem extends Item {
    public HousesHandMaskItem(Properties properties) {super(properties);}

    public static final Identifier BASE_LUCK_MODIFIER_ID = Identifier.withDefaultNamespace("base_luck");
    public static final Identifier BASE_ARMOR_MODIFIER_ID = Identifier.withDefaultNamespace("base_armor");
    public static final Identifier BASE_ARMOR_TOUGHNESS_MODIFIER_ID = Identifier.withDefaultNamespace("base_armor_toughness");

    public static ItemAttributeModifiers createAttributeModifiers() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.LUCK,
                        new AttributeModifier(BASE_LUCK_MODIFIER_ID, 1.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HEAD)
                .add(Attributes.ARMOR,
                        new AttributeModifier(BASE_ARMOR_MODIFIER_ID, 3.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HEAD)
                .add(Attributes.ARMOR_TOUGHNESS,
                        new AttributeModifier(BASE_ARMOR_TOUGHNESS_MODIFIER_ID, 2.0, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HEAD)
                .build();
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        if (itemStack.has(ModComponents.HOARD_AMOUNT)) {
            SavedGamblingData savedGamblingData = SavedGamblingData.getSavedGamblingData(level.getServer());
            int savedHoardAmount = savedGamblingData.getHousesHoard();
            if (itemStack.getOrDefault(ModComponents.HOARD_AMOUNT, 0) != savedHoardAmount) {
                itemStack.set(ModComponents.HOARD_AMOUNT, savedHoardAmount);
            }
        }
        super.inventoryTick(itemStack, level, owner, slot);
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity) {
        if (itemEntity.level() instanceof ServerLevel level) {
            level.addFreshEntity(new ItemEntity(level, 0, 320, 0, itemEntity.getItem()));
        }
    }
}
