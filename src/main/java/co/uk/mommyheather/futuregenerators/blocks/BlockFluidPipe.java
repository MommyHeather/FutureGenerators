package co.uk.mommyheather.futuregenerators.blocks;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import co.uk.mommyheather.futuregenerators.tile.TileFluidPipe;
import co.uk.mommyheather.futuregenerators.tile.Tiles;
import co.uk.mommyheather.futuregenerators.util.BlockSegment;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class BlockFluidPipe extends PipeBlock implements EntityBlock {
    
    public static final BooleanProperty PUMP_NORTH = BooleanProperty.create("pump_north");
    public static final BooleanProperty PUMP_EAST = BooleanProperty.create("pump_east");
    public static final BooleanProperty PUMP_SOUTH = BooleanProperty.create("pump_south");
    public static final BooleanProperty PUMP_WEST = BooleanProperty.create("pump_west");
    public static final BooleanProperty PUMP_UP = BooleanProperty.create("pump_up");
    public static final BooleanProperty PUMP_DOWN = BooleanProperty.create("pump_down");
    
    public static final Map<Direction, BooleanProperty> PUMP_BY_DIRECTION = ImmutableMap.copyOf(Util.make(Maps.newEnumMap(Direction.class), (p_55164_) -> {
        p_55164_.put(Direction.NORTH, PUMP_NORTH);
        p_55164_.put(Direction.EAST, PUMP_EAST);
        p_55164_.put(Direction.SOUTH, PUMP_SOUTH);
        p_55164_.put(Direction.WEST, PUMP_WEST);
        p_55164_.put(Direction.UP, PUMP_UP);
        p_55164_.put(Direction.DOWN, PUMP_DOWN);
    }));
    
    
    public static final BooleanProperty HIDE_STOPPERS = BooleanProperty.create("hide_stoppers");
    
    public BlockFluidPipe(Properties p_55160_) {
        super(0.15F, p_55160_); 
        this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, Boolean.valueOf(false)).setValue(EAST, Boolean.valueOf(false)).setValue(SOUTH, Boolean.valueOf(false))
        .setValue(WEST, Boolean.valueOf(false)).setValue(UP, Boolean.valueOf(false)).setValue(DOWN, Boolean.valueOf(false)).setValue(HIDE_STOPPERS, Boolean.valueOf(false))
        .setValue(PUMP_NORTH, false).setValue(PUMP_EAST, false).setValue(PUMP_SOUTH, false).setValue(PUMP_WEST, false)
        .setValue(PUMP_UP, false).setValue(PUMP_DOWN, false));
    }
    
    
    public BlockState updateShape(BlockState state, Direction direction, BlockState other, LevelAccessor accessor, BlockPos pos, BlockPos otherPos) {
        boolean flag = other.is(this) || isConnectable(accessor, other, pos, direction);
        state = state.setValue(PROPERTY_BY_DIRECTION.get(direction), Boolean.valueOf(flag));
        state = state.setValue(HIDE_STOPPERS, Boolean.valueOf(hideStoppers(state)));
        return state;
    }
    
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, PUMP_NORTH, PUMP_EAST, PUMP_SOUTH, PUMP_WEST, PUMP_UP, PUMP_DOWN, HIDE_STOPPERS);
    }
    
    
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.getStateForPlacement(context.getLevel(), context.getClickedPos());
    }
    
    public BlockState getStateForPlacement(BlockGetter getter, BlockPos pos) {
        BlockState blockstate = getter.getBlockState(pos.below());
        BlockState blockstate1 = getter.getBlockState(pos.above());
        BlockState blockstate2 = getter.getBlockState(pos.north());
        BlockState blockstate3 = getter.getBlockState(pos.east());
        BlockState blockstate4 = getter.getBlockState(pos.south());
        BlockState blockstate5 = getter.getBlockState(pos.west());
        BlockState state = this.defaultBlockState().setValue(DOWN, Boolean.valueOf(blockstate.is(this) || isConnectable(getter, blockstate, pos, Direction.DOWN)))
        .setValue(UP, Boolean.valueOf(blockstate1.is(this) || isConnectable(getter, blockstate1, pos, Direction.UP))).setValue(NORTH, Boolean.valueOf(blockstate2.is(this) || isConnectable(getter, blockstate2, pos, Direction.NORTH)))
        .setValue(EAST, Boolean.valueOf(blockstate3.is(this) || isConnectable(getter, blockstate3, pos, Direction.EAST))).setValue(SOUTH, Boolean.valueOf(blockstate4.is(this) || isConnectable(getter, blockstate4, pos, Direction.SOUTH)))
        .setValue(WEST, Boolean.valueOf(blockstate5.is(this) || isConnectable(getter, blockstate5, pos, Direction.WEST)));
        
        return state.setValue(HIDE_STOPPERS, Boolean.valueOf(hideStoppers(state)));
    }
    
    
    public boolean isConnectable(BlockGetter getter, BlockState state, BlockPos pos, Direction direction) {
        BlockEntity be = getter.getBlockEntity(pos.offset(direction.getStepX(), direction.getStepY(), direction.getStepZ()));
        if (be != null) return be.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite()).isPresent();
        return false;
    }
    
    public boolean hideStoppers(BlockState state) {
        for (Direction direction : new Direction[] {Direction.NORTH, Direction.EAST, Direction.UP}) {
            //this is where we check machines
            if (!state.getValue(PROPERTY_BY_DIRECTION.get(direction))) continue;
            if (!state.getValue(PROPERTY_BY_DIRECTION.get(direction.getOpposite()))) continue;
            return true;
        }
        return false;
    }
    
    
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileFluidPipe(pos, state);
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == Tiles.fluidPipe.get() ? TileFluidPipe::tick : null;
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
        if (!level.isClientSide && player.getItemInHand(hand).isEmpty() && hand == InteractionHand.MAIN_HAND) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof TileFluidPipe pipe) {
                HashMap<AABB, BlockSegment> segmentsMap = new HashMap<>();
                for (BlockSegment segment : BlockSegment.values()) {
                    if (segment != BlockSegment.CENTRE) {
                        BlockEntity be2 = level.getBlockEntity(pos.offset(segment.getDirection().getNormal()));
                        if (be2 == null || be2 instanceof TileFluidPipe) continue;
                    }
                    segmentsMap.put(new AABB(pos).deflate(0.34D, 0.34D, 0.34D).move(segment.xWithMult(0.34D), segment.yWithMult(0.34D), segment.zWithMult(0.34D)), segment); //divide into equal segments. each is technically a tad larger than a third of a block but that's fine
                }
                //sort these segments specifically so we're comparing the ones closer to the player first
                ArrayList<AABB> bbs = new ArrayList<>(segmentsMap.keySet());
                bbs.sort(new Comparator<AABB>() {
                    @Override
                    public int compare(AABB o1, AABB o2) {
                        return (int) ((o1.distanceToSqr(player.position()) - o2.distanceToSqr(player.position())) * 100);
                    }
                });
                
                for (AABB aabb : bbs) {
                    if (aabb.contains(trace.getLocation())) {
                        pipe.handleInteraction(segmentsMap.get(aabb), player.isCrouching(), player);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }
    
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean p_51542_) {
        if (!state.is(oldState.getBlock())) {
            TileFluidPipe pipe = (TileFluidPipe) level.getBlockEntity(pos);
            pipe.invalidateNetwork();
        }
        super.onRemove(state, level, pos, oldState, p_51542_);
    }
    
    
}
