package co.uk.mommyheather.futuregenerators.tile;

import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;

import co.uk.mommyheather.futuregenerators.config.FutureGeneratorsConfig;
import co.uk.mommyheather.futuregenerators.util.FutureGeneratorsEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class TilePump extends BlockEntity {


    public FluidTank tank;
    public FutureGeneratorsEnergyStorage battery;
    public ItemStackHandler items;

    private LazyOptional<IFluidHandler> lazyTank;
    private LazyOptional<IEnergyStorage> lazyBattery;
    private LazyOptional<IItemHandler> lazyItems;

    private boolean ticked = false;

    private ArrayList<BlockPos> pumpedBlocks = new ArrayList<>();
    public Fluid lastPumped = Fluids.EMPTY;

    private int ticks = 0;

    private static final Direction[] customDirections = new Direction[] {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN};


    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItems.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyTank.cast();
        }
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyBattery.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
        public void invalidateCaps() {
        super.invalidateCaps();
        lazyTank.invalidate();
        lazyBattery.invalidate();
        lazyItems.invalidate();
    }


    public TilePump(BlockPos p_155229_, BlockState p_155230_) {
        super(Tiles.pump.get(), p_155229_, p_155230_);
        tank = new FluidTank(0) {
            @Override
            protected void onContentsChanged()
            {
                setChanged();
            }
        };

        battery = new FutureGeneratorsEnergyStorage(0) {

            @Override
            public void onContentsChanged() {
                setChanged();
            }
            
        };
        
        items = new ItemStackHandler(1) {          
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack)
            {
                return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0 && !stack.hasCraftingRemainingItem();
            }
            
            @Override
            public void onContentsChanged(int slot) {
                setChanged();
            }
        };


        lazyTank = LazyOptional.of(() -> tank);
        lazyBattery = LazyOptional.of(() -> battery);
        lazyItems = LazyOptional.of(() -> items);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
    }

      
    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity be) {
        TilePump pump = (TilePump) be;
        if (!pump.ticked) {
            pump.tank.setCapacity(FutureGeneratorsConfig.SERVER.fluidPumpCapacity.get());
            pump.battery.setCapacity(FutureGeneratorsConfig.SERVER.fluidPumpBattery.get());
            pump.ticked = true;
        }
        if (level.isClientSide) return;

        pump.ticks++;
        if (pump.ticks % FutureGeneratorsConfig.SERVER.fluidPumpTime.get() != 0) return;

        pump.handlePowerConversion();


        BlockState below = level.getBlockState(pos.below());
        FluidState fluidState = below.getFluidState();

        //No point figuring out where to pump from or not if we have water underneath and aren't allowed to actually drain it
        if (!FutureGeneratorsConfig.SERVER.drainWater.get() && fluidState.is(Fluids.WATER)) {
            pump.drain(pos.below(), false);
        }
        else {
            pump.drain();
        }

    }

    public void drain() {
        if (pumpedBlocks.isEmpty()) {
            lastPumped = Fluids.EMPTY;

            if (isDrainable(worldPosition.below())) {
                if(drain(worldPosition.below(), true)) pumpedBlocks.add(worldPosition.below());
            }
            
        }
        else {
            //We work through these in reverse order. It's more performant.
            for (int i=pumpedBlocks.size()-1;i>=0;i--) {
                BlockPos pos = pumpedBlocks.get(i);
                for (Direction direction : customDirections) {
                    //if (direction == Direction.UP || direction == Direction.DOWN) continue;
                    BlockPos pos2 = pos.relative(direction);
                    if (isDrainable(pos2)) {
                        if (drain(pos2, true)) pumpedBlocks.add(pos2);
                        return;
                    }
                }
                //Nothing next to this block is drainable anymore! We can remove it, this helps with performance.
                pumpedBlocks.remove(i);
            }
        }
    }

    //Return true if a drain actually happened.
    public boolean drain(BlockPos pos, boolean consume) {
        if (!canDrain(level.getFluidState(pos))) return false;

        tank.fill(new FluidStack(level.getFluidState(pos).getType(), 1000), FluidAction.EXECUTE);
        battery.extractEnergy(FutureGeneratorsConfig.SERVER.fluidPumpConsumption.get(), false);

        lastPumped = level.getFluidState(pos).getType();

        if (consume) {
            level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }

        return consume;
    }

    public boolean isDrainable(BlockPos pos) {
        FluidState state = level.getFluidState(pos);

        //Any other checks we need?
        //We can't pump nothingness!
        if (state.isEmpty()) return false;
        //Different fluid to what we last pumped - only pump the same fluid!
        if (!state.is(lastPumped) && !lastPumped.isSame(Fluids.EMPTY)) return false;
        //We only pump source blocks!
        if (!state.isSource()) return false;
        //Is it within the defined range?
        if (worldPosition.distSqr(pos) > Math.pow(FutureGeneratorsConfig.SERVER.fluidPumpRange.get(), 2)) return false;
        //Is it within the allowed 3 block depth?
        if (pos.getY() <= worldPosition.getY() - 3) return false;

        return true;
    }

    public boolean canDrain(FluidState state) {
        //Any other checks we need?
        //Can't drain - tank is't empty, and fluid doesn't match what's in the tank. This should never happen!
        if (!tank.isEmpty() && !state.is(tank.getFluid().getFluid())) return false;
        //We drain 1000mb as a time. Does the tank have enough space?
        if (tank.getFluidAmount() + 1000 > tank.getCapacity()) return false;
        //Do we have enough power stored?
        if (battery.getEnergyStored() - FutureGeneratorsConfig.SERVER.fluidPumpConsumption.get() < 0) return false;

        return true;
    }

    public void handlePowerConversion() {
        if (items.getStackInSlot(0).isEmpty()) return;
        int toInsert = (int) (ForgeHooks.getBurnTime(items.getStackInSlot(0).copyWithCount(1), RecipeType.SMELTING) * FutureGeneratorsConfig.SERVER.fluidPumpConversion.get());
        if (battery.getMaxEnergyStored() - battery.getEnergyStored() < toInsert) return;
        battery.receiveEnergy(toInsert, false);
        items.extractItem(0, 1, false);
    }

    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("tank", tank.writeToNBT(new CompoundTag()));
        tag.put("battery", battery.serializeNBT());
        tag.put("items", items.serializeNBT());

        ListTag blocks = new ListTag();
        for (BlockPos blockPos : pumpedBlocks) {
            blocks.add(NbtUtils.writeBlockPos(blockPos));
        }
        tag.put("pumpedCache", blocks);
        
        tag.putString("fluid", ForgeRegistries.FLUIDS.getKey(lastPumped).toString());

    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("tank")) {
            tank.readFromNBT(tag.getCompound("tank"));
        }
        if (tag.contains("battery")) {
            battery.deserializeNBT(tag.get("battery"));
        }

        if (tag.contains("items")) {
            items.deserializeNBT(tag.getCompound("items"));
        }

        if (tag.contains("pumpedCache")) {
            ListTag blocks = tag.getList("pumpedCache", Tag.TAG_COMPOUND);
            for (int i=0;i<blocks.size();i++) {
                pumpedBlocks.add(NbtUtils.readBlockPos(blocks.getCompound(i)));
            }
        }

        if (tag.contains("fluid")) {
            lastPumped = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(tag.getString("fluid")));
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        
        tag.put("tank", tank.writeToNBT(new CompoundTag()));
        tag.put("battery", battery.serializeNBT());
        tag.putString("fluid", ForgeRegistries.FLUIDS.getKey(lastPumped).toString());
        
        return tag;
    }
    
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        if (tag.contains("tank")) {
            tank.readFromNBT(tag.getCompound("tank"));
        }
        if (tag.contains("battery")) {
            battery.deserializeNBT(tag.get("battery"));
        }

        if (tag.contains("fluid")) {
            lastPumped = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(tag.getString("fluid")));
        }
    }
    
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // Will get tag from #getUpdateTag
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
