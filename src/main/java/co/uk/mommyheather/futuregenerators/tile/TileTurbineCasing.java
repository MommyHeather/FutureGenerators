package co.uk.mommyheather.futuregenerators.tile;

import co.uk.mommyheather.futuregenerators.util.FutureGeneratorsEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public class TileTurbineCasing extends BlockEntity {
    
    
    public TileTurbineController controller;
    private boolean ticked = false;
    
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (controller == null) return super.getCapability(cap, side);
        return controller.getCapability(cap, side);
    }
    
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
    }
    
    
    public TileTurbineCasing(BlockPos p_155229_, BlockState p_155230_) {
        super(Tiles.turbineCasing.get(), p_155229_, p_155230_);
    }
    
    
    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity be) {
        if (level.isClientSide) return;
        TileTurbineCasing casing = (TileTurbineCasing) be;
        if (!casing.ticked) {
            casing.ticked = true;
            for (int x = pos.getX()-1;x<pos.getX()+2;x++) {
                for (int z = pos.getZ()-1;z<pos.getZ()+2;z++) {
                    if (x == pos.getX() && z == pos.getZ()) continue;
                    if (casing.controller != null) return;
                    BlockPos pos2 = new BlockPos(x, pos.getY(), z);
                    BlockEntity be2 = level.getBlockEntity(pos2);
                    if (be2 != null && be2 instanceof TileTurbineController controller) {
                        if (controller.valid) continue;
                        controller.validate();
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
            FutureGeneratorsEnergyStorage battery = controller.battery;
            if (battery.getEnergyStored() <=0) {
                return;
            }
            BlockEntity target = level.getBlockEntity(getBlockPos().relative(direction));
            if (target == null || target instanceof TileTurbineCasing || target instanceof TileTurbineController) { 
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
    }
    
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
    }
}
