package vance.profit.item.custom;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

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

}
