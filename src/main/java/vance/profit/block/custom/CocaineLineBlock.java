package vance.profit.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import vance.profit.codec.BlockSideEffects;
import vance.profit.effect.ModEffects;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CocaineLineBlock extends Block {
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    public CocaineLineBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
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
        builder.add(FACING);
    }
    @Override
    public @NonNull MapCodec<? extends SlotMachineBlock> codec() {return simpleCodec(SlotMachineBlock::new);}

    @Override
    protected @NonNull VoxelShape getShape(final @NonNull BlockState state, final @NonNull BlockGetter level, final @NonNull BlockPos pos, final @NonNull CollisionContext context) {
        return Block.column(10.0, 0.0, 1.0);
    }

    @Override
    protected boolean canSurvive(final @NonNull BlockState state, final LevelReader level, final BlockPos pos) {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        return this.canSurviveOn(level, below, belowState);
    }

    private boolean canSurviveOn(final BlockGetter level, final BlockPos relativePos, final BlockState relativeState) {
        return relativeState.isFaceSturdy(level, relativePos, Direction.UP);
    }


    @Override
    protected void neighborChanged(
            final @NonNull BlockState state, final Level level, final @NonNull BlockPos pos, final @NonNull Block block, final @Nullable Orientation orientation, final boolean movedByPiston
    ) {
        if (!level.isClientSide()) {
            if (!state.canSurvive(level, pos)) {
                dropResources(state, level, pos);
                level.removeBlock(pos, false);
            }

        }
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        level.playSound(
                null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.SNIFFER_SNIFFING, SoundSource.PLAYERS, 1.0f, 1.0f
        );
        List<BlockSideEffects> sideEffects = getBlockSideEffects(level.getBlockState(pos.below()).getBlock());
        if (sideEffects != null) {
            for (BlockSideEffects record : sideEffects) {
                record.sideEffects().forEach(effect -> {
                    int recordDuration = player.hasEffect(effect) ? Objects.requireNonNull(player.getEffect(effect)).getDuration() + 400 : record.duration();
                    player.addEffect(new MobEffectInstance(effect, recordDuration, record.strength()));
                });
            }
        }
        player.removeEffect(MobEffects.WEAKNESS);
        player.removeEffect(MobEffects.MINING_FATIGUE);
        player.removeEffect(MobEffects.HUNGER);

        int increasingAmplifier = player.hasEffect(ModEffects.STIMULATED) ? Objects.requireNonNull(player.getEffect(ModEffects.STIMULATED)).getAmplifier() + 1 : 0;
        int increasingDuration = player.hasEffect(ModEffects.STIMULATED) ?
                Objects.requireNonNull(player.getEffect(ModEffects.STIMULATED)).getDuration() + 400 : 1800;

        player.addEffect(new MobEffectInstance(ModEffects.STIMULATED, increasingDuration, increasingAmplifier));
        level.removeBlock(pos, false);
        return InteractionResult.SUCCESS;
    }

    private List<BlockSideEffects> getBlockSideEffects(Block block) {
        return BlockSideEffects.BLOCK_SIDE_EFFECTS.stream().filter(blockSideEffects ->
                blockSideEffects.blockState().is(block)).collect(Collectors.toList());
    }
}
