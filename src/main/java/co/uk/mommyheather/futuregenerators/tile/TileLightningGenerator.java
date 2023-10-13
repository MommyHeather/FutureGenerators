package co.uk.mommyheather.futuregenerators.tile;

import co.uk.mommyheather.futuregenerators.config.FutureGeneratorsConfig;
import co.uk.mommyheather.futuregenerators.items.Items;
import co.uk.mommyheather.futuregenerators.util.FutureGeneratorsEnergyStorage;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
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
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class TileLightningGenerator extends BlockEntity {

    
    public FutureGeneratorsEnergyStorage battery;
    public ItemStackHandler items;

    private boolean ticked = false;

    private LazyOptional<IEnergyStorage> lazyBattery;
    private LazyOptional<IItemHandler> lazyItems;

    private int ticks = 0;

    public boolean hasRod = false;
    public int dynamos = 0;


    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItems.cast();
        }
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyBattery.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
        public void invalidateCaps() {
        super.invalidateCaps();
        lazyItems.invalidate();
        lazyBattery.invalidate();
    }


    public TileLightningGenerator(BlockPos p_155229_, BlockState p_155230_) {
        super(Tiles.lightningGenerator.get(), p_155229_, p_155230_);

        items = new ItemStackHandler(6) {          
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack)
            {
                return stack.is(Items.lightningCharge.get());
            }
        };

        battery = new FutureGeneratorsEnergyStorage(9, Integer.MAX_VALUE, 0);

        lazyBattery = LazyOptional.of(() -> battery);
        lazyItems = LazyOptional.of(() -> items);
    }

    
    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity be) {
        if (level.isClientSide) return;
        TileLightningGenerator generator = (TileLightningGenerator) be;

        ItemStack fuel = ItemStack.EMPTY;

        if (!generator.ticked) {
            generator.battery.setCapacity(FutureGeneratorsConfig.SERVER.lightningGeneratorCapacity.get());
            
            generator.checkNeighbours();
            generator.ticked = true;
        }

        for (int i=0;i<6;i++) {
            fuel = generator.items.getStackInSlot(i);
            if (!fuel.isEmpty()) break;
        }

        if (!fuel.isEmpty() && generator.hasRod && generator.battery.getEnergyStored() >= FutureGeneratorsConfig.SERVER.lightningGeneratorConsumption.get()) {
            generator.ticks++;

            generator.battery.setEnergy(generator.battery.getEnergyStored() - FutureGeneratorsConfig.SERVER.lightningGeneratorConsumption.get());

            if (generator.ticks % 20 == 0) {
                if (fuel.hurt(1, level.random, null)) {
                    fuel.setCount(0);
                }

            }
            ((ServerLevel) level).sendParticles(ParticleTypes.FIREWORK, (double)pos.getX() + level.random.nextDouble(), (double)(pos.getY() + 1), (double)pos.getZ() + level.random.nextDouble(), 2, 0.3D, 0.0D, 0.3D, 0.1D);

            generator.setChanged();

        }

    }

    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        
        tag.put("items", items.serializeNBT());
        tag.put("battery", battery.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("items")) {
            items.deserializeNBT(tag.getCompound("items"));
        }
        if (tag.contains("battery")) {
            battery.deserializeNBT(tag.get("battery"));
        } 
    }


    public void checkNeighbours() {
        dynamos = 0;

        BlockState rod = level.getBlockState(worldPosition.above());
        hasRod = rod.is(Blocks.LIGHTNING_ROD);

        for (Direction direction : Direction.values()) {
            if (direction.getAxis() == Axis.Y) continue;
            BlockState dynamo = level.getBlockState(worldPosition.relative(direction));
            if (dynamo.is(co.uk.mommyheather.futuregenerators.blocks.Blocks.lightningDynamo.get())) {
                dynamos++;
            }
        }

    }

    public boolean isRunning() {
        
        ItemStack fuel = ItemStack.EMPTY;
        for (int i=0;i<6;i++) {
            fuel = items.getStackInSlot(i);
            if (!fuel.isEmpty()) break;
        }

        return (!fuel.isEmpty() && hasRod && battery.getEnergyStored() >= FutureGeneratorsConfig.SERVER.lightningGeneratorConsumption.get());
    }
    

}
