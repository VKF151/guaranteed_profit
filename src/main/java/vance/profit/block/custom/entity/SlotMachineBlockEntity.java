package vance.profit.block.custom.entity;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import vance.profit.Guaranteed_profit;
import vance.profit.codec.AcceptedCurrencies;
import vance.profit.inventory.SlotMachineInventory;
import vance.profit.world.ModGameRules;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static net.minecraft.world.level.block.Block.*;
import static vance.profit.block.custom.SlotMachineBlock.WIN;

public class SlotMachineBlockEntity extends BlockEntity implements SlotMachineInventory, WorldlyContainer {
    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    private boolean WON = false;
    private long lastActivatedTime = -1;

    public SlotMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SLOT_MACHINE_ENTITY, pos, state);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public @Nullable ItemStack removeItemNoUpdate(int slot) {
        items.set(slot, ItemStack.EMPTY);
        setChanged();
        return null;
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput view) {
        super.saveAdditional(view);
        ContainerHelper.saveAllItems(view, items);
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput view) {
        super.loadAdditional(view);
        ContainerHelper.loadAllItems(view, items);
    }

    public void playGame(BlockPos pos, Level world, Player player) {


        if (!world.isClientSide()) {
            int slotMachineChance = Objects.requireNonNull(world.getServer()).getGameRules().get(ModGameRules.SLOT_MACHINE_CHANCE_GAMERULE);
            int bonusChance = player.hasEffect(MobEffects.LUCK) ? (Objects.requireNonNull(player.getEffect(MobEffects.LUCK)).getAmplifier() + 1): 0;

            boolean win = world.getRandom().nextInt(slotMachineChance) + bonusChance >= slotMachineChance - 1;

            BlockState currentState = world.getBlockState(pos);
            world.setBlock(pos, currentState.setValue(WIN, win), Block.UPDATE_ALL);
            world.playSound(
                    null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.NOTE_BLOCK_BASS, SoundSource.BLOCKS, 1.0f, 1.0f
            );
            if (win) {
                setWON(true);
                world.playSound(
                        null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.NOTE_BLOCK_PLING, SoundSource.BLOCKS, 1.0f, 1.0f
                );
                for (ItemStack itemStack : getWonItem(SlotMachineBlockEntity.this)) {
                    popResource(world, pos.above(), itemStack);
                }



            } else {
                setWON(false);
                world.setBlockAndUpdate(pos, currentState.setValue(WIN, false));
                world.playSound(
                        null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.NOTE_BLOCK_SNARE, SoundSource.BLOCKS, 1.0f, 1.0f
                );
            } setChanged();
        }
    }

    private static List<ItemStack> getWonItem(BlockEntity entity) {
        assert entity.getLevel() != null;
        if (!(entity instanceof SlotMachineBlockEntity slotMachine)) {
            return Collections.emptyList();
        }
        ItemStack internalStack = slotMachine.getItem(0);
        if (internalStack.isEmpty()) {
            return Collections.emptyList();
        }
        Item internalCurrency = internalStack.getItem();
        String itemName = BuiltInRegistries.ITEM.getKey(internalCurrency).getPath();
        Identifier lootTableId = Identifier.fromNamespaceAndPath(Guaranteed_profit.MOD_ID, "rewards/slot_machine/" + itemName);
        LootTable lootTable = Objects.requireNonNull(entity.getLevel().getServer()).reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, lootTableId));
        return lootTable.getRandomItems(
                new LootParams.Builder((ServerLevel) entity.getLevel())
                        .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(entity.getBlockPos()))
                        .create(LootContextParamSets.CHEST)
        );
    }

    public  boolean getWon() {
        return WON;
    }
    public void setWON(boolean WON) {
        this.WON = WON;
    }

    public long getLastActivatedTime() {
        return lastActivatedTime;
    }
    public void setLastActivatedTime(long lastActivatedTime) {
        this.lastActivatedTime = lastActivatedTime;
    }

    @Override
    public int @NonNull [] getSlotsForFace(@NonNull Direction side) {
        return new int[]{0};
    }
    public boolean canPlaceItem(int slot, @NonNull ItemStack stack) {
        return slot == 0 && (stack.isEmpty() || AcceptedCurrencies.ACCEPTED_CURRENCIES.contains(stack.getItem()));
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, @NonNull ItemStack stack, Direction direction) {
        return canPlaceItem(slot, stack);
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, @NonNull ItemStack stack, @NonNull Direction direction) {
        return slot == 0 && AcceptedCurrencies.ACCEPTED_CURRENCIES.contains(stack.getItem());
    }

}
