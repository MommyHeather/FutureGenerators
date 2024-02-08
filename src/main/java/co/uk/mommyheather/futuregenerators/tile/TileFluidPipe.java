package co.uk.mommyheather.futuregenerators.tile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

import org.jetbrains.annotations.NotNull;

import co.uk.mommyheather.futuregenerators.FutureGenerators;
import co.uk.mommyheather.futuregenerators.blocks.BlockFluidPipe;
import co.uk.mommyheather.futuregenerators.config.FutureGeneratorsConfig;
import co.uk.mommyheather.futuregenerators.util.BlockSegment;
import co.uk.mommyheather.futuregenerators.util.FluidPipeNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class TileFluidPipe extends BlockEntity {
    
    
    public FakeFluidHandler tank;
    
    private LazyOptional<IFluidHandler> lazyTank;
    
    private HashMap<Direction, Boolean> pullingModes = new HashMap<>();
    
    public FluidPipeNetwork network;

    public int priority = 0;

    private int ticks = 0;

    
    ArrayList<TileFluidPipe> pipes = new ArrayList<TileFluidPipe>();
    
    
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (isConnectedToTank()) {
            if (cap == ForgeCapabilities.FLUID_HANDLER) {
                return LazyOptional.of(() -> tank.of(side)).cast();
            }

        }
        return super.getCapability(cap, side);
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyTank.invalidate();
    }
    
    
    public TileFluidPipe(BlockPos pos, BlockState state) {
        super(Tiles.fluidPipe.get(), pos, state);
        tank = new FakeFluidHandler(this);
        
        
        lazyTank = LazyOptional.of(() -> tank);
        for (Direction direction : Direction.values()) {
            pullingModes.put(direction, state.getValue(BlockFluidPipe.PUMP_BY_DIRECTION.get(direction)));
        }
    }
    
    @Override
    public void setChanged() {
        super.setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
    }

    public void invalidateNetwork() {
        if (network != null) {
            network.removePipe(worldPosition.asLong());
        }
    }
    
    
    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity be) {
        if (level.isClientSide) return;
        TileFluidPipe pipe = (TileFluidPipe) be;
        
        if (pipe.network == null) {
            //Time to get a network! We'll start with searching the pipes in each direction.
            //If we get multiple networks, it's merging time.
            //If we get no networks, we create a new one.
            
            HashMap<Long, FluidPipeNetwork> networks = new HashMap<>();
            for (Direction direction : Direction.values()) {     
                BlockEntity blockEntity = level.getBlockEntity(pos.offset(direction.getNormal()))  ;     
                if (blockEntity != null && (blockEntity instanceof TileFluidPipe neighbour)) {
                    if (neighbour.network == null || networks.containsKey(neighbour.network.id)) continue;
                    networks.put(neighbour.network.id, neighbour.network);
                }
            }

            //No networks found. Time for a new one!
            //Should only happen if a pipe is placed alone.
            if (networks.isEmpty()) {
                pipe.network = new FluidPipeNetwork(pos.asLong());
                pipe.network.addPipe(pos.asLong(), pipe);
            }
            //One network found. Insert ourselves into it!
            else if (networks.size() == 1) {
                FluidPipeNetwork network = networks.entrySet().iterator().next().getValue();
                pipe.network = network;
                network.addPipe(pos.asLong(), pipe);
            }
            //Multiple networks found. This can happen if a pipe is placed between two other pipes.
            //Merge time.
            else {
                Entry<Long, FluidPipeNetwork> entry = networks.entrySet().iterator().next();
                networks.remove(entry.getKey());
                entry.getValue().addPipe(pos.asLong(), pipe);
                pipe.network = entry.getValue();
                entry.getValue().mergeIntoSelf(networks.values().toArray(new FluidPipeNetwork[networks.size()]));
            }
            
        }

        pipe.ticks++;
        if (pipe.ticks % FutureGeneratorsConfig.SERVER.fluidPipeFrequency.get() == 0) {
            for (Direction direction : Direction.values()) {
                if (!pipe.pullingModes.get(direction)) continue;

                BlockPos pos2 = pos.offset(direction.getNormal());
                BlockEntity target = level.getBlockEntity(pos2);
                if (target != null && !(target instanceof TileFluidPipe)) {
                    LazyOptional<IFluidHandler> optional = target.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite());
                    if (optional.isPresent()) {
                        IFluidHandler handler = optional.resolve().get();
                        FluidStack toFill = handler.drain(FutureGeneratorsConfig.SERVER.fluidPipeRate.get(), FluidAction.SIMULATE);
                        int delivered = pipe.distributeFluidIntoNetwork(toFill, FluidAction.SIMULATE, direction);

                        toFill = handler.drain(delivered, FluidAction.EXECUTE);
                        pipe.distributeFluidIntoNetwork(toFill, FluidAction.EXECUTE, direction);
                        
                    }
                }
                
            }
        }
    }

    public void updateCache() {
        if (!network.hasChanged(worldPosition)) return;
        pipes = new ArrayList<TileFluidPipe>(network.pipes.values());
        pipes.sort(new Comparator<TileFluidPipe>() {

            @Override
            public int compare(TileFluidPipe o1, TileFluidPipe o2) {
                int result = o2.priority - o1.priority; //reversed here because higher priority should go first
                if (result != 0) return result;
                return (int) ((worldPosition.distSqr(o1.worldPosition) - worldPosition.distSqr(o2.worldPosition)) * 100);  //multiplying here before the int cast gives us a better accuracy
            }
            
        });
    }
    
    public int distributeFluidIntoNetwork(FluidStack fluid, FluidAction action, Direction exclude) {
        //sort the pipes.
        if (network == null) return 0; //we can't accept if we have no network
        updateCache();

        fluid = fluid.copy();
        int delivered = 0;
        for (TileFluidPipe pipe : pipes) {
            if (fluid.getAmount() <= 0) return delivered;
            fluid.setAmount(fluid.getAmount() - delivered);
            delivered += pipe.distributeFluidIntoConnections(fluid, action, exclude, worldPosition.asLong());
        }
        return delivered;
    }

    public int distributeFluidIntoConnections(FluidStack fluid, FluidAction action, Direction exclude, long source) {

        fluid = fluid.copy();

        int delivered = 0;

        for (Direction direction : Direction.values()) {
            if (direction == exclude && worldPosition.asLong() == source) continue; //skip the side we're pulling / receiving from
            if (pullingModes.get(direction)) continue; //don't push / pull the same block
            if (fluid.getAmount() <= 0) return delivered;
            fluid.setAmount(fluid.getAmount() - delivered);
            BlockPos pos = worldPosition.offset(direction.getNormal());
            BlockEntity be = level.getBlockEntity(pos);
            if (be != null && !(be instanceof TileFluidPipe)) {
                LazyOptional<IFluidHandler> optional = be.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite());
                if (optional.isPresent()) {
                    IFluidHandler handler = optional.resolve().get();
                    delivered += handler.fill(fluid, action);
                    
                }
            }
        }
        return delivered;
    }
    
    public boolean isConnectedToTank() {
        
        for (Direction direction : Direction.values()) {
            BlockPos pos = worldPosition.offset(direction.getNormal());
            BlockEntity be = level.getBlockEntity(pos);
            if (be != null && !(be instanceof TileFluidPipe)) {
                if (be.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite()).isPresent()) return true;
            }
        }
        
        return false;
    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("priority", priority);
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("priority")) {
            priority = tag.getInt("priority");
        }
    }

    
    public void handleInteraction(BlockSegment blockSegment, boolean crouching, Player player) {
        switch (blockSegment) {
            case CENTRE : {
                priority += crouching ? -1 : 1;
                player.sendSystemMessage(Component.translatable("futuregenerators.interactions.priority", priority));
                network.setChanged();
                return;
            }
            default : {
                Boolean mode = !pullingModes.get(blockSegment.getDirection());
                pullingModes.put(blockSegment.getDirection(), mode);
                BlockState state = level.getBlockState(worldPosition).setValue(BlockFluidPipe.PUMP_BY_DIRECTION.get(blockSegment.getDirection()), mode);
                level.setBlockAndUpdate(worldPosition, state);
                player.sendSystemMessage(Component.translatable("futuregenerators.interactions.pulling", mode));
                network.setChanged();
                return;
            }

        }
    }
    
    //Does the client need to know this?
    //It's disabled for now!
    /*
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        
        CompoundTag pulls = new CompoundTag();
        for (int i=0; i<6; i++) {
            pulls.putBoolean(Direction.values()[i].getName(), pullingModes.get(i));
        }
        tag.put("pullingModes", pulls);
        
        return tag;
    }
    
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        
        if (tag.contains("pullingModes")) {
            pullingModes.clear();
            CompoundTag pulls = tag.getCompound("pullingModes");
            for (int i=0; i<6; i++) {
                pullingModes.add(pulls.getBoolean(Direction.values()[i].getName()));
            }
        }
    }
    /*
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // Will get tag from #getUpdateTag
        return ClientboundBlockEntityDataPacket.create(this);
    }
    */
    
    
    public static class FakeFluidHandler implements IFluidHandler {
        
        public final TileFluidPipe pipe;
        public Direction direction;
        
        public FakeFluidHandler(TileFluidPipe pipe) {
            this.pipe = pipe;
        }

        public FakeFluidHandler(TileFluidPipe pipe, Direction side) {
            this.pipe = pipe;
            this.direction = side;
        }

        public FakeFluidHandler of(Direction direction) {
            return new FakeFluidHandler(pipe, direction);
        }
        
        @Override
        public int getTanks() {
            return 1;
        }
        
        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            return FluidStack.EMPTY;
        }
        
        @Override
        public int getTankCapacity(int tank) {
            return Integer.MAX_VALUE;
        }
        
        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return true;
        }
        
        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return pipe.distributeFluidIntoNetwork(resource, action, direction);
        }
        
        //Can't drain out of these!
        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            return FluidStack.EMPTY;
        }
        
        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            return FluidStack.EMPTY;
        }
        
    }

}
