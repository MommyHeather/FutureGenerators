package co.uk.mommyheather.futuregenerators.tile;

import java.util.ArrayList;

import co.uk.mommyheather.futuregenerators.config.FutureGeneratorsConfig;
import co.uk.mommyheather.futuregenerators.fluids.Fluids;
import co.uk.mommyheather.futuregenerators.util.FutureGeneratorsEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraft.server.level.ServerLevel;

public class TileTurbineController extends BlockEntity {
    
    
    public int speed;
    public FluidTank tank;
    public FutureGeneratorsEnergyStorage battery;
    
    private boolean ticked = false;
    
    private LazyOptional<IFluidHandler> lazyTank;
    private LazyOptional<IEnergyStorage> lazyBattery;
    
    private int ticks = 0;
    
    public boolean valid = false;
    public ArrayList<TileTurbineCasing> casings = new ArrayList<>();
    
    
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
    
    @Override
    public void setRemoved() {
        super.setRemoved();
        this.valid = false;
        casings.forEach((casing) -> {
            casing.controller = null;
        });
    }
    
    
    public TileTurbineController(BlockPos p_155229_, BlockState p_155230_) {
        super(Tiles.turbineController.get(), p_155229_, p_155230_);
        speed = 0;
        
        tank = new FluidTank(0, (stack) -> {
            return stack.getFluid().isSame(Fluids.HOT_WATER.get());
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
        TileTurbineController turbine = (TileTurbineController) be;
        
        
        if (!turbine.ticked) {
            turbine.tank.setCapacity(FutureGeneratorsConfig.SERVER.multiblockTurbineWaterCapacity.get());
            
            turbine.battery.setCapacity(FutureGeneratorsConfig.SERVER.multiblockTurbinePowerCapacity.get());
            
            if (!turbine.valid) turbine.validate();
            turbine.ticked = true;
        }
        
        if (turbine.tank.getFluidAmount() > 0 && turbine.tank.getFluidAmount() >= turbine.speed) {
            
            //consume
            turbine.tank.drain(turbine.speed, FluidAction.EXECUTE);
            
            //spin up
            if (turbine.speed < FutureGeneratorsConfig.SERVER.multiblockTurbineMaxSpeed.get()) {
                turbine.speed = Math.min(FutureGeneratorsConfig.SERVER.multiblockTurbineMaxSpeed.get(),
                turbine.speed + Math.max(1,
                FutureGeneratorsConfig.SERVER.multiblockTurbineMaxSpeed.get() / (FutureGeneratorsConfig.SERVER.multiblockTurbineSpinupTime.get() * 20)
                ));
            }
        }
        else {
            //spin down, but can't consume
            turbine.speed = Math.max(0, 
            turbine.speed - Math.max(1,
            FutureGeneratorsConfig.SERVER.multiblockTurbineMaxSpeed.get() / (FutureGeneratorsConfig.SERVER.multiblockTurbineSpinupTime.get() * 20)
            )
            );
        }
        turbine.battery.receiveEnergy(turbine.speed * FutureGeneratorsConfig.SERVER.multiblockTurbineFeRatio.get(), false);
        
        turbine.ticks++;
        if (turbine.ticks % 6 == 0 || turbine.speed > 0) {
            turbine.eject();
        }
    }
    
    public void validate() {
        valid = false;
        casings.forEach((casing) -> {
            casing.controller = null;
        });
        casings.clear();
        
        BlockEntity be;
        
        //We'll go NESW, skipping up and down checking the next in the list if we find a valid casing
        for (Direction direction : Direction.values()) {
            if (direction == Direction.UP || direction == Direction.DOWN) continue;
            BlockPos first = worldPosition.offset(direction.getNormal());
            be = level.getBlockEntity(first);
            if (be != null && be instanceof TileTurbineCasing firstCasing) {
                if (firstCasing.controller != null) continue;
                //Here, we now have the first casing, or have moved to the next direction if there's no valid casing here.
                BlockPos second = worldPosition.offset(direction.getClockWise().getNormal());
                be = level.getBlockEntity(second);
                if (be != null && be instanceof TileTurbineCasing secondCasing) {
                    if (secondCasing.controller != null) continue;
                    //and now we also have the second, if it's valid!
                    BlockPos third = second.offset(direction.getNormal());
                    be = level.getBlockEntity(third);
                    if (be != null && be instanceof TileTurbineCasing thirdCasing) {
                        if (thirdCasing.controller != null) continue;
                        //We have three casings here!
                        //We can now say we're valid, yay.
                        
                        //Particle for self
                        ((ServerLevel) level).sendParticles(ParticleTypes.FIREWORK, (double)worldPosition.getX() + level.random.nextDouble(), (double)(worldPosition.getY() + 1), 
                        (double)worldPosition.getZ() + level.random.nextDouble(), 2, 0.3D, 0.0D, 0.3D, 0.1D);
                        
                        valid = true;
                        casings.add(firstCasing);
                        ((ServerLevel) level).sendParticles(ParticleTypes.FIREWORK, (double)firstCasing.getBlockPos().getX() + level.random.nextDouble(), (double)(firstCasing.getBlockPos().getY() + 1), 
                        (double)firstCasing.getBlockPos().getZ() + level.random.nextDouble(), 2, 0.3D, 0.0D, 0.3D, 0.1D);
                        firstCasing.controller = this;
                        
                        casings.add(secondCasing);
                        ((ServerLevel) level).sendParticles(ParticleTypes.FIREWORK, (double)secondCasing.getBlockPos().getX() + level.random.nextDouble(), (double)(secondCasing.getBlockPos().getY() + 1), 
                        (double)secondCasing.getBlockPos().getZ() + level.random.nextDouble(), 2, 0.3D, 0.0D, 0.3D, 0.1D);
                        secondCasing.controller = this;
                        
                        casings.add(thirdCasing);
                        ((ServerLevel) level).sendParticles(ParticleTypes.FIREWORK, (double)thirdCasing.getBlockPos().getX() + level.random.nextDouble(), (double)(thirdCasing.getBlockPos().getY() + 1), 
                        (double)thirdCasing.getBlockPos().getZ() + level.random.nextDouble(), 2, 0.3D, 0.0D, 0.3D, 0.1D);
                        thirdCasing.controller = this;
                        return;
                    }
                }
            }
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
            if (target == null || target instanceof TileTurbineController || target instanceof TileTurbineCasing) { 
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
