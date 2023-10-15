package co.uk.mommyheather.futuregenerators.tile;

import co.uk.mommyheather.futuregenerators.config.FutureGeneratorsConfig;
import co.uk.mommyheather.futuregenerators.util.FutureGeneratorsEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class TileTurbine extends BlockEntity {


    public int speed;
    public FluidTank tank;
    public FutureGeneratorsEnergyStorage battery;

    private boolean ticked = false;

    private LazyOptional<IFluidHandler> lazyTank;
    private LazyOptional<IEnergyStorage> lazyBattery;

    private int ticks = 0;


    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
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
    }


    public TileTurbine(BlockPos p_155229_, BlockState p_155230_) {
        super(Tiles.turbine.get(), p_155229_, p_155230_);
        speed = 0;

        tank = new FluidTank(0, (stack) -> {
            return stack.getFluid().isSame(Fluids.WATER);
        }) {
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

        lazyTank = LazyOptional.of(() -> tank);
        lazyBattery = LazyOptional.of(() -> battery);
    }

    
    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity be) {
        if (level.isClientSide) return;
        TileTurbine turbine = (TileTurbine) be;


        if (!turbine.ticked) {
            turbine.tank.setCapacity(FutureGeneratorsConfig.SERVER.turbineWaterCapacity.get());
    
            turbine.battery.setCapacity(FutureGeneratorsConfig.SERVER.turbinePowerCapacity.get());

            turbine.ticked = true;
        }

        if (turbine.tank.getFluidAmount() > 0 && turbine.tank.getFluidAmount() >= turbine.speed) {
            
            //consume
            turbine.tank.drain(turbine.speed, FluidAction.EXECUTE);

            //spin up
            if (turbine.speed < FutureGeneratorsConfig.SERVER.turbineMaxSpeed.get()) {
                turbine.speed = Math.min(FutureGeneratorsConfig.SERVER.turbineMaxSpeed.get(),
                    turbine.speed + Math.max(1,
                        FutureGeneratorsConfig.SERVER.turbineMaxSpeed.get() / (FutureGeneratorsConfig.SERVER.turbineSpinupTime.get() * 20)
                    ));
            }
        }
        else {
            //spin down, but can't consume
            turbine.speed = Math.max(0, 
                turbine.speed - Math.max(1,
                    FutureGeneratorsConfig.SERVER.turbineMaxSpeed.get() / (FutureGeneratorsConfig.SERVER.turbineSpinupTime.get() * 20)
                )
            );
        }
        turbine.battery.receiveEnergy(turbine.speed * FutureGeneratorsConfig.SERVER.turbineFeRatio.get(), false);

        turbine.ticks++;
        if (turbine.ticks % 6 == 0 || turbine.speed > 0) {
            turbine.eject();
        }
    }


    public void eject() {
        
        // eject
        for (Direction direction : new Direction[] {
            Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST, Direction.UP, Direction.DOWN
        }) {
            if (battery.getEnergyStored() <=0) {
                return;
            }
            BlockEntity target = level.getBlockEntity(getBlockPos().relative(direction));
                if (target == null) { 
                continue;
            }
            LazyOptional<IEnergyStorage> energyOptional = target.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite());
            if (energyOptional == null || !energyOptional.isPresent()) { 
                continue;
            }
            IEnergyStorage storage = energyOptional.resolve().get();
            int energy = storage.receiveEnergy(battery.getEnergyStored(), true);
            if (energy != 0) {
                storage.receiveEnergy(energy, false);
                battery.setEnergy(battery.getEnergyStored() - energy);
            }
        }
    }

    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("tank", tank.writeToNBT(new CompoundTag()));
        tag.put("battery", battery.serializeNBT());

        tag.putInt("speed", speed);
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
        
        speed = tag.getInt("speed");
    }
}
