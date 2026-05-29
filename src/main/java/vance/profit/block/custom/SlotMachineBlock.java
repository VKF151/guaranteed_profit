package vance.profit.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.redstone.Orientation;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import vance.profit.block.custom.entity.SlotMachineBlockEntity;
import vance.profit.item.ModItems;

public class SlotMachineBlock extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty WIN = BooleanProperty.create("win");
    public SlotMachineBlock(BlockBehaviour.Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WIN, false));
    }
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }
    @Override
    protected @NonNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected @NonNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WIN);
    }

    @Override
    public @NonNull MapCodec<? extends SlotMachineBlock> codec() {return simpleCodec(SlotMachineBlock::new);}

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new SlotMachineBlockEntity(pos, state);
    }

    @Override
    public void animateTick(BlockState state, @NonNull Level world, @NonNull BlockPos pos, @NonNull RandomSource random) {
        state.getValue(FACING);
    }

    @Override
    public @NonNull RenderShape getRenderShape(@NonNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected @NonNull InteractionResult useItemOn(@NonNull ItemStack stack, @NonNull BlockState state, Level world, @NonNull BlockPos pos, @NonNull Player player, @NonNull InteractionHand hand, @NonNull BlockHitResult hit) {
        if (world.isClientSide()) return InteractionResult.SUCCESS;

        if (!(world.getBlockEntity(pos) instanceof SlotMachineBlockEntity blockEntity)) {
            return InteractionResult.PASS;
        }

        ItemStack handStack = player.getItemInHand(hand);

        ItemStack storedStack = blockEntity.getItem(0);
        if (!handStack.isEmpty() && handStack.getItem() == Items.DIAMOND && !player.isShiftKeyDown()) {
            if (storedStack.isEmpty()) {
                int insertAmount = Math.min(handStack.getCount(), handStack.getMaxStackSize());
                blockEntity.setItem(0, handStack.split(insertAmount));
            } else if (
                    storedStack.getItem() == handStack.getItem() &&
                            storedStack.getCount() < storedStack.getMaxStackSize()
            ) {
                int space = storedStack.getMaxStackSize() - storedStack.getCount();
                int insertAmount = Math.min(handStack.getCount(), space);

                if (insertAmount > 0) {
                    storedStack.grow(insertAmount);
                    handStack.shrink(insertAmount);
                    blockEntity.setItem(0, storedStack);
                }
            }

            blockEntity.setChanged();
            return InteractionResult.SUCCESS;
        } else if (!handStack.isEmpty() && handStack.getItem() == ModItems.SLOT_SPINNER){
            blockEntity.playGame(pos, world);
            if (!player.hasInfiniteMaterials()) handStack.shrink(1);
        } else {
            if (!storedStack.isEmpty()) {
                player.getInventory().placeItemBackInInventory(storedStack.copy());
                blockEntity.setItem(0, ItemStack.EMPTY);
                blockEntity.setChanged();
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;

    }

    protected void affectNeighborsAfterRemoval(@NonNull BlockState state, @NonNull ServerLevel world, @NonNull BlockPos pos, boolean moved) {
        Containers.updateNeighboursAfterDestroy(state, world, pos);
    }


    @Override
    protected void neighborChanged(@NonNull BlockState state, Level world, @NonNull BlockPos pos, @NonNull Block sourceBlock, @Nullable Orientation wireOrientation, boolean notify) {
        if (!world.isClientSide() && world.hasNeighborSignal(pos)) {
            if (world.getBlockEntity(pos) instanceof SlotMachineBlockEntity blockEntity) {
                ItemStack storedStack = blockEntity.getItem(0);

                long currentTime = world.getGameTime();
                if (blockEntity.getLastActivatedTime() + 5 <= currentTime) {
                    blockEntity.setLastActivatedTime(currentTime);

                    if (!storedStack.isEmpty() && storedStack.getCount() > 0) {
                            storedStack.shrink(1);
                            blockEntity.playGame(pos, world);

                    }
                }


            }
        }
    }



}
