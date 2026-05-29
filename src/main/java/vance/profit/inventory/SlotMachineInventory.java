package vance.profit.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface SlotMachineInventory extends Container {

    NonNullList<ItemStack> getItems();

    static SlotMachineInventory of(NonNullList<ItemStack> items) {
        return new SlotMachineInventory() {
            @Override
            public @Nullable ItemStack removeItemNoUpdate(int slot) {
                return null;
            }

            private final NonNullList<ItemStack> inventoryItems = items;

            @Override
            public NonNullList<ItemStack> getItems() {
                return inventoryItems;
            }
        };
    }

    static SlotMachineInventory ofSize(int size) {
        return of(NonNullList.withSize(size, ItemStack.EMPTY));
    }

    @Override
    default int getContainerSize() {
        return getItems().size();
    }

    @Override
    default boolean isEmpty() {
        for (ItemStack stack : getItems()) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    default @NonNull ItemStack getItem(int slot) {
        return getItems().get(slot);
    }

    @Override
    default @NonNull ItemStack removeItem(int slot, int count) {
        ItemStack result = ContainerHelper.removeItem(getItems(), slot, count);
        if (!result.isEmpty()) {
            setChanged();
        }
        return result;
    }

    @Override
    default void setItem(int slot, @NonNull ItemStack stack) {
        getItems().set(slot, stack);
        if (stack.getCount() > stack.getMaxStackSize()) {
            stack.setCount(stack.getMaxStackSize());
        }
        setChanged();
    }

    @Override
    default void clearContent() {
        getItems().clear();
        setChanged();
    }

    @Override
    default void setChanged() {

    }

    @Override
    default boolean stillValid(@NonNull Player player) {
        return true;
    }

}
