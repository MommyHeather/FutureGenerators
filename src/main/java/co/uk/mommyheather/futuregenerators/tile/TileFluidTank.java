package co.uk.mommyheather.futuregenerators.tile;

import co.uk.mommyheather.futuregenerators.config.FutureGeneratorsConfig;
import co.uk.mommyheather.futuregenerators.fluids.Fluids;
import co.uk.mommyheather.futuregenerators.util.FutureGeneratorsEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
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

public class TileFluidTank extends BlockEntity {


    public FluidTank tank;

    private LazyOptional<IFluidHandler> lazyTank;

    private boolean ticked = false;


    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyTank.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
        public void invalidateCaps() {
        super.invalidateCaps();
        lazyTank.invalidate();
    }


    public TileFluidTank(BlockPos p_155229_, BlockState p_155230_) {
        super(Tiles.fluidTank.get(), p_155229_, p_155230_);
        tank = new FluidTank(0) {
            @Override
            protected void onContentsChanged()
            {
                setChanged();
            }
        };


        lazyTank = LazyOptional.of(() -> tank);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
    }

      
    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity be) {
        //if (level.isClientSide) return; //needn't be enabled?
        TileFluidTank tank = (TileFluidTank) be;


        if (!tank.ticked) {
            tank.tank.setCapacity(FutureGeneratorsConfig.SERVER.fluidTankCapacity.get());
    

            tank.ticked = true;
        }
    }

    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("tank", tank.writeToNBT(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("tank")) {
            tank.readFromNBT(tag.getCompound("tank"));
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        
        tag.put("tank", tank.writeToNBT(new CompoundTag()));
        
        return tag;
    }
    
    @Override
    public void handleUpdateTag(CompoundTag tag) {
        if (tag.contains("tank")) {
            tank.readFromNBT(tag.getCompound("tank"));
        }
    }
    
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        // Will get tag from #getUpdateTag
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
