package co.uk.mommyheather.futuregenerators.tile;

import java.util.HashMap;

import co.uk.mommyheather.futuregenerators.blocks.Blocks;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public class TileLightningDynamo extends BlockEntity {

    
    public FutureGeneratorsEnergyStorage battery;

    private boolean ticked = false;

    private LazyOptional<IEnergyStorage> lazyBattery;

    private int ticks = 0;

    public boolean hasRod = false;
    public int dynamos = 0;

    public HashMap<Direction, TileLightningGenerator> generators = new HashMap<>();


    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyBattery.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
        public void invalidateCaps() {
        super.invalidateCaps();
        lazyBattery.invalidate();
    }


    public TileLightningDynamo(BlockPos p_155229_, BlockState p_155230_) {
        super(Tiles.lightningDynamo.get(), p_155229_, p_155230_);

        battery = new FutureGeneratorsEnergyStorage(0, 0, Integer.MAX_VALUE);

        lazyBattery = LazyOptional.of(() -> battery);
    }

    
    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity be) {
        if (level.isClientSide) return;

        TileLightningDynamo dynamo = (TileLightningDynamo) be;
        if (!dynamo.ticked) {
            dynamo.ticked = true;
            dynamo.battery.setCapacity(FutureGeneratorsConfig.SERVER.lightningDynamoCapacity.get());
            dynamo.checkNeighbours();
        }
        int i = 0;

        for (Direction direction : Direction.values()) {
            if (dynamo.generators.get(direction) != null) {
                TileLightningGenerator generator = dynamo.generators.get(direction);
                if (generator.isRunning()) {
                    i++;
                    if (i > FutureGeneratorsConfig.SERVER.lightningDynamoMaxGenerators.get()) break;
                    dynamo.battery.setEnergy(dynamo.battery.getEnergyStored() + FutureGeneratorsConfig.SERVER.lightningDynamoProduction.get());
                }
            }
        }

        dynamo.ticks++;
        if (dynamo.ticks % 6 == 0) {
            dynamo.eject();
        }

    }

    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        
        tag.put("battery", battery.serializeNBT());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("battery")) {
            battery.deserializeNBT(tag.get("battery"));
        } 
    }


    public void eject() {
        
        // eject
        for (Direction direction : Direction.values()) {
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

    public void checkNeighbours() {
        for (Direction direction : Direction.values()) {
            if (direction.getAxis() == Axis.Y) continue;
            BlockState generator = level.getBlockState(worldPosition.relative(direction));
            if (generator.is(Blocks.lightningGenerator.get())) {
                generators.put(direction, (TileLightningGenerator) level.getBlockEntity(worldPosition.relative(direction)));
            }
            else {
                generators.put(direction, null);
            }
        }

    }

}
