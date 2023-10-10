package co.uk.mommyheather.futuregenerators.tile;

import co.uk.mommyheather.futuregenerators.config.FutureGeneratorsConfig;
import co.uk.mommyheather.futuregenerators.util.FutureGeneratorsEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
        });

        battery = new FutureGeneratorsEnergyStorage(0);

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

    }
}
