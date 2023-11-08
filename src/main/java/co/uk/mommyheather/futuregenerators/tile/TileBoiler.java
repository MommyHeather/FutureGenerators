package co.uk.mommyheather.futuregenerators.tile;

import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;

import co.uk.mommyheather.futuregenerators.config.FutureGeneratorsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileBoiler extends BlockEntity {

    public DoubleTank tank;
    public ItemStackHandler items;

    private boolean ticked = false;

    private LazyOptional<IFluidHandler> lazyTank;
    private LazyOptional<IItemHandler> lazyItems;

    private int ticks = 0;

    public int fuelRemaining = 0;


    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItems.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyTank.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
        public void invalidateCaps() {
        super.invalidateCaps();
        lazyTank.invalidate();
        lazyItems.invalidate();
    }


    public TileBoiler(BlockPos p_155229_, BlockState p_155230_) {
        super(Tiles.boiler.get(), p_155229_, p_155230_);

        tank = new DoubleTank(0, (stack) -> {
            return stack.getFluid().isSame(Fluids.WATER);
        }) {
            @Override
            protected void onContentsChanged()
            {
                setChanged();
            }
        };
        
        items = new ItemStackHandler(1) {          
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack)
            {
                return ForgeHooks.getBurnTime(stack, RecipeType.SMELTING) > 0;
            }
            
            @Override
            public void onContentsChanged(int slot) {
                setChanged();
            }
        };

        lazyTank = LazyOptional.of(() -> tank);
        lazyItems = LazyOptional.of(() -> items);
    }

    
    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity be) {
        if (level.isClientSide) return;
        TileBoiler boiler = (TileBoiler) be;


        if (!boiler.ticked) {
            boiler.tank.setCapacity(FutureGeneratorsConfig.SERVER.boilerWaterCapacity.get(), 2);


            boiler.ticked = true;
        }

        boiler.ticks++;

        if (boiler.fuelRemaining <= 0) {
            if (boiler.tank.inTank.getFluidAmount() > 0 && boiler.tank.outTank.getFluidAmount() < boiler.tank.outTank.getCapacity()) {
                //Only start a new burn under three conditions - burn time is empty, water tank is not empty, and hot water tank is not full
                ItemStack stack = boiler.items.extractItem(0, 1, false);
                boiler.fuelRemaining = ForgeHooks.getBurnTime(stack, RecipeType.SMELTING);
            }
        }
        else {
            boiler.fuelRemaining--; // burns fuel
            boiler.tank.drainIntake(FutureGeneratorsConfig.SERVER.boilerHeatingRate.get());
            boiler.tank.fillOutput(new FluidStack(co.uk.mommyheather.futuregenerators.fluids.Fluids.HOT_WATER.get(), FutureGeneratorsConfig.SERVER.boilerHeatingRate.get()));
        }

        if (boiler.ticks % 6 == 0 || boiler.fuelRemaining > 0) {
            boiler.eject();
        }

    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("tank", tank.writeToNBT(new CompoundTag()));
        tag.put("items", items.serializeNBT());

        tag.putInt("burnTime", fuelRemaining);

    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("tank")) {
            tank.readFromNBT(tag.getCompound("tank"));
        }
        if (tag.contains("items")) {
            items.deserializeNBT(tag.getCompound("items"));
        }
        if (tag.contains("burnTime")) {
            this.fuelRemaining = tag.getInt("burnTime");
        }
    }

    

    public void eject() {
        
        // eject
        for (Direction direction : new Direction[] {
            Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN
        }) {
            if (tank.outTank.getFluidAmount() <=0) {
                return;
            }
            BlockEntity target = level.getBlockEntity(getBlockPos().relative(direction));
                if (target == null) { 
                continue;
            }
            LazyOptional<IFluidHandler> fluidOptional = target.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite());
            if (fluidOptional == null || !fluidOptional.isPresent()) { 
                continue;
            }
            IFluidHandler storage = fluidOptional.resolve().get();
            int amount = storage.fill(tank.outTank.getFluid(), FluidAction.SIMULATE);
            if (amount != 0) {
                amount = storage.fill(tank.outTank.getFluid(), FluidAction.EXECUTE);
                tank.drain(amount, FluidAction.EXECUTE);
            }
        }
    }




    public static class DoubleTank implements IFluidHandler {
        
        private FluidTank inTank;
        private FluidTank outTank;
        
        public DoubleTank(int capacity, Predicate<FluidStack> validatorInsert)
        {
            this.inTank = new FluidTank(capacity, validatorInsert);
            this.outTank = new FluidTank(capacity);
        }

        @Override
        public int getTanks() {
            return 2;
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            return tank == 0? inTank.getFluid() : outTank.getFluid();
        }

        @Override
        public int getTankCapacity(int tank) {
            return inTank.getCapacity();
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return tank == 0? inTank.isFluidValid(stack) : outTank.isFluidValid(stack);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            onContentsChanged();
            return inTank.fill(resource, action);
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            onContentsChanged();
            return outTank.drain(resource, action);
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            onContentsChanged();
            return outTank.drain(maxDrain, action);
        }

        //mod-only methods

        public void drainIntake(int toDrain) {
            onContentsChanged();
            inTank.drain(toDrain, FluidAction.EXECUTE);
        }

        public void fillOutput(FluidStack resource) {
            onContentsChanged();
            outTank.fill(resource, FluidAction.EXECUTE);
        }

        
        public void setCapacity(int capacity, int outMult)
        {
            inTank.setCapacity(capacity);
            outTank.setCapacity(capacity * outMult);
        }

        protected void onContentsChanged() {

        }

        
        public void readFromNBT(CompoundTag compound) {
            if (compound.contains("in")) {
                inTank.readFromNBT(compound.getCompound("in"));
            }
            if (compound.contains("out")) {
                outTank.readFromNBT(compound.getCompound("out"));
            }
        }

        public Tag writeToNBT(CompoundTag tag) {
            CompoundTag in = new CompoundTag();
            inTank.writeToNBT(in);
            tag.put("in", in);
            
            CompoundTag out = new CompoundTag();
            outTank.writeToNBT(out);
            tag.put("out", out);

            return tag;
        }

    }
}
