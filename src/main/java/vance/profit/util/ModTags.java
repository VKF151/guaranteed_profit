package vance.profit.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.Identifier;
import vance.profit.Guaranteed_profit;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> NEEDS_CRAZY_SLOTS_TOOL =
                createTag("needs_crazy_slots_tool");

        public static final TagKey<Block> INCORRECT_FOR_CRAZY_SLOTS_TOOL =
                createTag("incorrect_for_crazy_slots_tool");

        private static TagKey<Block> createTag(String name) {
            return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, name));
        }
    }
    public static class Items {
        public static final TagKey<Item> CRAZY_SLOTS_REPAIR = createTag("crazy_slots_repair");

        private static TagKey<Item> createTag(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, name));
        }
    }
}
