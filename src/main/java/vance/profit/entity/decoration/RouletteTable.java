package vance.profit.entity.decoration;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Continuation;
import net.minecraft.util.Prediction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.decoration.BlockAttachedEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import vance.profit.item.ModItems;
import vance.profit.world.SavedGamblingData;

import java.util.*;

import static vance.profit.codec.AcceptedCurrencies.ACCEPTED_CURRENCIES;

public class RouletteTable extends BlockAttachedEntity{
    private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.defineId(RouletteTable.class, EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Integer> DATA_GRID_X = SynchedEntityData.defineId(RouletteTable.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_GRID_Y = SynchedEntityData.defineId(RouletteTable.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> RESULT = SynchedEntityData.defineId(RouletteTable.class, EntityDataSerializers.INT);
    private int miniGameTicks = 30;
    public record LocationCoordinate(int x, int y) {}
    public Map<LocationCoordinate, String> coordToBet = new HashMap<>();

    public RouletteTable(EntityType<? extends BlockAttachedEntity> type, Level level) {
        super(type, level);
    }

    @Override
    public void dropItem(final ServerLevel level, final @Nullable Entity causedBy) {
        this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
        this.showBreakingParticles();
        if (level.getGameRules().get(GameRules.ENTITY_DROPS)) {
            if (!(causedBy instanceof Player player && player.hasInfiniteMaterials())) {
                ItemEntity itemEntity = this.spawnAtLocation(level, this.getTableItemStackWithData());
                ItemEntity betItemEntity = this.spawnAtLocation(level, this.getItem());
                if (itemEntity != null && causedBy instanceof LightningBolt) {
                    itemEntity.setInvulnerableTime(20);
                }
            }
        }
    }

    private static boolean isBreakingAllowedFor(final DamageSource source) {
        return !(source.getEntity() instanceof Player player) || player.mayBuild();
    }

    private boolean isBreakingDeniedAtPosFor(final ServerLevel level, final DamageSource source) {
        return source.getEntity() instanceof Player player && !level.mayInteract(player, this.pos);
    }

    @Override
    public boolean hurtServer(final @NonNull ServerLevel level, final @NonNull DamageSource source, final float damage) {
        return isBreakingAllowedFor(source) && !this.isBreakingDeniedAtPosFor(level, source) && super.hurtServer(level, source, damage);
    }

    @Override
    public boolean hurtClient(final @NonNull DamageSource source) {
        return isBreakingAllowedFor(source) && super.hurtClient(source);
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ModItems.ROULETTE_TABLE);
    }

    private void showBreakingParticles() {
        if (this.level() instanceof ServerLevel level) {
            level.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, Blocks.WOOL.pick(DyeColor.GREEN).defaultBlockState()),
                    this.getX(),
                    this.getY(0.6666666666666666),
                    this.getZ(),
                    10,
                    this.getBbWidth() / 4.0F,
                    this.getBbHeight() / 4.0F,
                    this.getBbWidth() / 4.0F,
                    0.05
            );
        }
    }

    @Override
    protected void recalculateBoundingBox() {
        this.setBoundingBox(calculateBoundingBox(this.makeBoundingBox(), this.getDirection()));
    }

    protected static AABB calculateBoundingBox(final AABB aabb, final Direction direction) {
        Direction.Axis axis = direction.getAxis();
        double xSize = axis == Direction.Axis.X ? 2 : 4;
        double ySize = aabb.getYsize();
        double zSize = axis == Direction.Axis.Z ? 2 : 4;
        Vec3 pos = aabb.getCenter().relative(direction, 0.5).relative(direction.getClockWise(), 1.5);
        return AABB.ofSize(pos, xSize, ySize, zSize);
    }

    @Override
    protected void tickAtCheckInterval() {
        if (this.level() instanceof ServerLevel level) {
            BlockPos blockPos = this.blockPosition();
            FluidState fluidState = level.getBlockState(blockPos).getFluidState();
            if (this.collidedWithFluid(fluidState, blockPos, this.position(), this.position())) {
                fluidState.entityInside(level, blockPos, this, this.insideEffectCollector);
                this.insideEffectCollector.applyAndClear(this);
            }

            this.destroyIfInFire(level);
        }
    }

    public void destroyIfInFire(final ServerLevel level) {
        if (!this.isRemoved()) {
            level.findBlocksIn(this.getBoundingBox().nextDeflated()).filterState(state -> state.is(BlockTags.FIRE)).forEachUntil((var2, var3) -> {
                this.hurtServer(level, this.damageSources().inFire(), 1.0F);
                return Continuation.ABORT;
            });
        }
    }

    @Override
    public boolean survives() {
        return wouldSurviveAt(this.level(), this.getBoundingBox());
    }

    public static boolean canBePlacedAt(final Level level, final AABB boundingBox) {
        return wouldSurviveAt(level, boundingBox) && !isAnchorBuried(level, boundingBox);
    }

    private static boolean isAnchorBuried(final Level level, final AABB boundingBox) {
        AABB restingSlice = new AABB(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.minY + 0.015625, boundingBox.maxZ)
                .nextDeflated();
        VoxelShape exposedSurface = Shapes.create(restingSlice);

        for (VoxelShape collider : level.getBlockCollisions(null, restingSlice)) {
            exposedSurface = Shapes.join(exposedSurface, collider, BooleanOp.ONLY_FIRST);
            if (exposedSurface.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void setPos(final double x, final double y, final double z) {
        this.setPosRaw(x, y, z);
        super.setPos(x, y, z);
    }

    public static boolean wouldSurviveAt(final Level level, final AABB boundingBox) {
        return hasAnchorBelow(level, boundingBox) && !isCoveredBySuffocatingBlocks(level, boundingBox);
    }

    private static boolean hasAnchorBelow(final Level level, final AABB boundingBox) {
        AABB anchorBox = new AABB(
                boundingBox.minX, boundingBox.minY - 0.015625, boundingBox.minZ, Math.nextDown(boundingBox.maxX), boundingBox.minY, Math.nextDown(boundingBox.maxZ)
        );
        return level.findBlocksIn(anchorBox.expandTowards(0.0, -0.125, 0.0)).forEachUntil((blockPos, blockState) -> {
            VoxelShape shape = blockState.getShape(level, blockPos);
            return !shape.isEmpty() && shape.bounds().move(blockPos).intersects(anchorBox) ? Continuation.ABORT : Continuation.CONTINUE;
        });
    }

    private static boolean isCoveredBySuffocatingBlocks(final Level level, final AABB boundingBox) {
        for (BlockPos blockPos : BlockPos.betweenClosed(boundingBox.nextDeflated())) {
            if (!level.getBlockState(blockPos).isSuffocating(level, blockPos)) {
                return false;
            }
        }

        return true;
    }

    public ItemStack getItem() {
        return this.getEntityData().get(DATA_ITEM);
    }

    public void setItem(ItemStack item) {this.getEntityData().set(DATA_ITEM, item);}

    public LocationCoordinate getCoord() {return new LocationCoordinate(this.getEntityData().get(DATA_GRID_X), this.getEntityData().get(DATA_GRID_Y));}

    public void setDataGridX(int i) {this.getEntityData().set(DATA_GRID_X, i);}
    public void setDataGridY(int i) {this.getEntityData().set(DATA_GRID_Y, i);}

    @Override
    protected void addAdditionalSaveData(final @NonNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        ItemStack currentItem = this.getItem();
        LocationCoordinate currentCoords = this.getGridCoords();
        int currentResult = this.getResult();
        if (!currentItem.isEmpty()) {
            output.store("Item", ItemStack.CODEC, currentItem);
            output.store("slot_grid_X", Codec.INT, currentCoords.x());
            output.store("slot_grid_Y", Codec.INT, currentCoords.y());
            output.store("result", Codec.INT, currentResult);
        }
    }

    @Override
    protected void readAdditionalSaveData(final @NonNull ValueInput input) {
        super.readAdditionalSaveData(input);
        ItemStack itemStack = input.read("Item", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        LocationCoordinate coordinates = new LocationCoordinate(input.read("slot_grid_X", Codec.INT).orElse(0), input.read("slot_grid_Y", Codec.INT).orElse(0));
        Integer i = input.read("result", Codec.INT).orElse(0);
        setItem(itemStack);
        setDataGridX(coordinates.x);
        setDataGridY(coordinates.y);
        setResult(i);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NonNull Builder entityData) {
        entityData.define(DATA_ITEM, ItemStack.EMPTY);
        entityData.define(DATA_GRID_X, 0);
        entityData.define(DATA_GRID_Y, 0);
        entityData.define(RESULT, 0);

    }

    private ItemStack getTableItemStackWithData() {
        ItemStack itemStack = new ItemStack(ModItems.ROULETTE_TABLE);
        itemStack.set(DataComponents.CUSTOM_NAME, this.getCustomName());
        itemStack.set(DataComponents.CUSTOM_NAME, this.getCustomName());
        return itemStack;
    }

    @Override
    public @NonNull InteractionResult interact(final Player player, final @NonNull InteractionHand hand, final @NonNull Vec3 location) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (player.isSpectator()) {
            return InteractionResult.SUCCESS;
        }

        if (player.level().isClientSide()) {
            return InteractionResult.SUCCESS_SERVER;
        }
        LocationCoordinate locationCoordinate = getCoordsForLocation(location);
        if (locationCoordinate.x() < 0 || locationCoordinate.x() > 10 || locationCoordinate.y() < 0 || locationCoordinate.y() > 5) {
            return InteractionResult.FAIL;
        }


        boolean isValid = !itemStack.isEmpty() && ACCEPTED_CURRENCIES.contains(itemStack.getItem());
        ItemStack storedStack = this.getEntityData().get(DATA_ITEM);
        if (isValid && !player.isShiftKeyDown()) {
            if (storedStack.isEmpty()) {
                int insertAmount = Math.min(itemStack.getCount(), itemStack.getMaxStackSize());
                if (!itemStack.isEmpty()) {
                    this.playSound(this.getAddItemSound(), 1.0F, 1.0F, location);
                }
                setItem(itemStack.split(insertAmount));
            } else if (
                    storedStack.getItem() == itemStack.getItem() &&
                            storedStack.getCount() < storedStack.getMaxStackSize()
            ) {
                int space = storedStack.getMaxStackSize() - storedStack.getCount();
                int insertAmount = Math.min(itemStack.getCount(), space);

                if (insertAmount > 0) {
                    if (!itemStack.isEmpty()) {
                        this.playSound(this.getAddItemSound(), 1.0F, 1.0F, location);
                    }
                    storedStack.grow(insertAmount);
                    itemStack.shrink(insertAmount);
                    setItem(storedStack);
                }
            }
            setDataGridX(locationCoordinate.x);
            setDataGridY(locationCoordinate.y);
            return InteractionResult.SUCCESS;
        } else {
            if (!storedStack.isEmpty()) {
                player.getInventory().placeItemBackInInventory(storedStack.copy(), Prediction.PREDICTED);
                this.playSound(this.getRemoveItemSound(), 1.0F, 1.0F, location);
                setItem(ItemStack.EMPTY);
                this.getEntityData().set(DATA_GRID_X, locationCoordinate.x());
                this.getEntityData().set(DATA_GRID_Y, locationCoordinate.y());
                return InteractionResult.SUCCESS;
            }
        }
        return super.interact(player, hand, location);
    }
    
    public LocationCoordinate getCoordsForLocation(Vec3 location) {
        Direction facing = this.getDirection();
        double pixelSize = 0.0625;
        int northMod = facing == Direction.NORTH ? -1 : 1;
        int westMod = facing == Direction.WEST ? -1 : 1;
        int southMod = facing == Direction.SOUTH ? -1 : 1;
        int gridY = (int) (((facing == Direction.NORTH || facing == Direction.SOUTH) ? (location.z * northMod)/pixelSize : (location.x * westMod)/pixelSize) + 2) /3;
        int gridX = (int) (((facing == Direction.WEST || facing == Direction.EAST) ? (location.z * westMod)/pixelSize : (location.x * southMod)/pixelSize) - 21) /3;
        return setToProperLocation(new LocationCoordinate(gridX, gridY));
    }
    
    private LocationCoordinate setToProperLocation(LocationCoordinate coordinate) {
        int x = coordinate.x;
        int y = coordinate.y;

        if (x == 0 && (y % 2 != 0)) {
            return new LocationCoordinate(x, y - 1);
        }
        if (y == 0 && (x == 2 || x == 4 || x == 7 || x == 9)) {
            return new LocationCoordinate(x - 1, y);
        }
        if (y == 1 && ((x + 1) % 3 != 0) && x != 10) {
            return new LocationCoordinate(x % 3 == 0 ? x - 1 : x + 1 , y);
        }
        if (x == 10 && y == 1) {
            return new LocationCoordinate(x, 0);
        }
        return coordinate;
    }

    public LocationCoordinate getGridCoords() {
        return new LocationCoordinate(this.getEntityData().get(DATA_GRID_X), this.getEntityData().get(DATA_GRID_Y));
    }
    
    public SoundEvent getAddItemSound() {
        return SoundEvents.ITEM_FRAME_ADD_ITEM;
    }
    public SoundEvent getRemoveItemSound() {
        return SoundEvents.ITEM_FRAME_REMOVE_ITEM;
    }

    public void playSound(SoundEvent sound, float volume, float pitch, Vec3 location) {
        if (!this.isSilent()) {
            this.level().playSound(null, this.getX() + location.x, this.getY(), this.getZ() + location.z, sound, this.getSoundSource(), volume, pitch);
        }

    }

    @Override
    public void tick() {
        primeWhenOnPoweredPosition();
        super.tick();
    }
    
    private void primeWhenOnPoweredPosition() {
        if (this.level() instanceof ServerLevel level) {
            BlockPos here = BlockPos.containing(this.position());
            ItemStack stack = this.getItem();
            if (this.miniGameTicks > 0 && level.getBestOwnOrNeighbourSignal(here) != 0 && !stack.isEmpty() && stack.getCount() > 0) {
                miniGameTicks--;
                if (this.miniGameTicks % 2 == 0 && miniGameTicks > 8) {
                    float pitch = 1.0f;
                    if (this.miniGameTicks % 4 == 0) {
                        pitch = 1.05f;
                    }
                    makeGameStartNoise(level, pitch);
                }
                if (this.miniGameTicks == 0) {
                    this.playGame(level, stack);
                }
            }
        }
    }
    public void makeBets() {
        for (int i = 0; i < 9; i ++) {
            for (int j = 0; j < 4; j ++) {
                this.coordToBet.put(new LocationCoordinate(i + 1, j + 2), String.valueOf((i * 4) + j + 1));
            }
        }
        this.coordToBet.put(new LocationCoordinate(0, 0), "1 to 18");
        this.coordToBet.put(new LocationCoordinate(0, 2), "0");
        this.coordToBet.put(new LocationCoordinate(0, 4), "00");

        this.coordToBet.put(new LocationCoordinate(1, 0), "Even");
        this.coordToBet.put(new LocationCoordinate(3, 0), "Red");
        this.coordToBet.put(new LocationCoordinate(6, 0), "Black");
        this.coordToBet.put(new LocationCoordinate(8, 0), "Odd");

        this.coordToBet.put(new LocationCoordinate(2, 1), "1st 12");
        this.coordToBet.put(new LocationCoordinate(5, 1), "2nd 12");
        this.coordToBet.put(new LocationCoordinate(8, 1), "3rd 12");

        this.coordToBet.put(new LocationCoordinate(10, 0), "19 to 36");
        this.coordToBet.put(new LocationCoordinate(10, 2), "R1");
        this.coordToBet.put(new LocationCoordinate(10, 3), "R2");
        this.coordToBet.put(new LocationCoordinate(10, 4), "R3");
        this.coordToBet.put(new LocationCoordinate(10, 5), "R4");
    }
    public Map<LocationCoordinate, String> getCoordToBet() {
        return coordToBet;
    }
    public int getResult() {
        return this.getEntityData().get(RESULT);
    }
    public void setResult(int i) {
        this.getEntityData().set(RESULT, i);
    }
    public void makeGameStartNoise(ServerLevel level, float pitch) {
        level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.NOTE_BLOCK_SNARE, SoundSource.BLOCKS, 1.0f, pitch);
    }
    
    public void playGame(ServerLevel level, ItemStack betStack) {
        if (this.miniGameTicks == 0) {
            this.miniGameTicks = 30;
        }
        int outcome = level.getRandom().nextInt(38);
        setResult(outcome);
        List<Integer> numbersBetOn = getNumbersBetOn();
        boolean won = numbersBetOn.stream().anyMatch(n -> n == outcome);
        if (won) {
            level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.NOTE_BLOCK_PLING, SoundSource.BLOCKS, 1.0f, 1.0f);
            int amountWon = betStack.count() * (36/numbersBetOn.size());
            while (amountWon > 0) {
                if (amountWon > 64) {
                    ItemEntity stackOfWinnings = this.spawnAtLocation(level, betStack.copyWithCount(64));
                    amountWon -= 64;
                } else {
                    ItemEntity winnings = this.spawnAtLocation(level, betStack.copyWithCount(amountWon));
                    amountWon = 0;
                }
            }
        } else {
            level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.NOTE_BLOCK_BASEDRUM, SoundSource.BLOCKS, 1.0f, 0.9f);
            SavedGamblingData data = SavedGamblingData.getSavedGamblingData(level.getServer());
            data.incrementHousesHoard(betStack.count());

        }
        setItem(ItemStack.EMPTY);
        System.out.println(outcome);
    }
    public List<Integer> getNumbersBetOn() {
        List<Integer> numbersBetOn =  new ArrayList<>();
        int x = getCoord().x;
        int y = getCoord().y;
        makeBets();
        String placedBet = this.coordToBet.get(getCoord());
        if ((x >=1 && x <= 9 && y >= 2) || (x == 0 && y == 2)) {
            int betNumber = Integer.parseInt(placedBet);
            numbersBetOn.add(betNumber);
            return numbersBetOn;
        }
        if (x == 0) {
            if (y == 0) {
                for (int i = 1; i <= 18; i++) {
                    numbersBetOn.add(i);
                }
                return numbersBetOn;
            }
            if (y == 4) {
                numbersBetOn.add(37);
                return numbersBetOn;
            }
        }
        if (y == 0) {
            if (x == 1) {
                for (int i = 1; i <= 36; i++) {
                    if (i % 2 == 0) {
                        numbersBetOn.add(i);
                    }
                }
                return numbersBetOn;
            }
            if (x == 3) {
                return Arrays.asList(1, 3, 5 ,7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36);
            }
            if (x == 6) {
                return Arrays.asList(2, 4, 6, 8, 10, 11, 13, 15, 17, 20, 22, 24, 26, 28, 29, 31, 33, 35);
            }
            if (x == 8) {
                for (int i = 1; i <= 36; i++) {
                    if (i % 2 == 1) {
                        numbersBetOn.add(i);
                    }
                }
                return numbersBetOn;
            }
        }
        if (y == 1) {
            if (x == 2) {
                for (int i = 1; i <= 12; i++) {
                    numbersBetOn.add(i);
                }
                return numbersBetOn;
            }
            if (x == 5) {
                for (int i = 13; i <= 24; i++) {
                    numbersBetOn.add(i);
                }
                return numbersBetOn;
            }
            if (x == 8) {
                for (int i = 25; i <= 36; i++) {
                    numbersBetOn.add(i);
                }
                return numbersBetOn;
            }
        }
        if (x == 10) {
            if (y == 0) {
                for (int i = 19; i <= 36; i++) {
                    numbersBetOn.add(i);
                }
                return numbersBetOn;
            }
            if (y >= 2) {
                for (int i = 1; i <= 9; i++) {
                    numbersBetOn.add(Integer.parseInt(this.coordToBet.get(new LocationCoordinate(i, y))));
                }
                return numbersBetOn;
            }
        }
        return numbersBetOn;

    }

}
