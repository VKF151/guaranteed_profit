package vance.profit.components;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import vance.profit.Guaranteed_profit;

import java.util.List;
import java.util.function.UnaryOperator;

public class ModComponents {

    public static final DataComponentType<ItemStack> ORIGINALITEM =
            register("original_item", itemStackBuilder -> itemStackBuilder.persistent(ItemStack.CODEC));

    public static final DataComponentType<Boolean> TRANSFORMABLE =
            register("transformable", booleanBuilder ->  booleanBuilder.persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL));

    public static final DataComponentType<Integer> WEAPON_ID =
            register("weapon_id", integerBuilder -> integerBuilder.persistent(ExtraCodecs.POSITIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final DataComponentType<List<ItemEnchantments>> TRANSFORMABLE_ENCHANTS =
            register("transformable_enchants", listBuilder -> listBuilder.persistent(ItemEnchantments.CODEC.listOf()).cacheEncoding());

    public static final DataComponentType<Integer> ABILITY_USES =
            register("ability_uses", integerBuilder -> integerBuilder.persistent(ExtraCodecs.POSITIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));

    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, name),
                builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void initialize() {
        Guaranteed_profit.LOGGER.info("Registering {} components", Guaranteed_profit.MOD_ID);
    }

}
