package co.uk.mommyheather.futuregenerators.tile;

import java.util.HashMap;

import org.jetbrains.annotations.NotNull;

import co.uk.mommyheather.futuregenerators.config.FutureGeneratorsConfig;
import co.uk.mommyheather.futuregenerators.fluids.Fluids;
import co.uk.mommyheather.futuregenerators.util.FutureGeneratorsEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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

public class TileWasher extends BlockEntity {

    public FluidTank tank;
    public FutureGeneratorsEnergyStorage battery;
    public ItemStackHandler items;

    private boolean ticked = false;

    private LazyOptional<IFluidHandler> lazyTank;
    private LazyOptional<IEnergyStorage> lazyBattery;
    private LazyOptional<IItemHandler> lazyItems;

    private int ticks = 0;


    private static HashMap<ItemStack, Float> drops = new HashMap<>();
    static {
        drops.put(new ItemStack(Items.IRON_NUGGET), 0.15F);
        drops.put(new ItemStack(Items.GOLD_NUGGET), 0.15F);
        drops.put(new ItemStack(Items.IRON_ORE), 0.1F);
        drops.put(new ItemStack(Items.GOLD_ORE), 0.1F);
        drops.put(new ItemStack(Items.QUARTZ), 0.05F);
        drops.put(new ItemStack(Items.LAPIS_LAZULI), 0.05F);
        drops.put(new ItemStack(Items.REDSTONE), 0.05F);
        drops.put(new ItemStack(Items.DIAMOND), 0.015F);
        drops.put(new ItemStack(Items.ENDER_PEARL), 0.015F);
        drops.put(new ItemStack(Items.NETHERITE_SCRAP), 0.001F);
    }


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
    }


    public TileWasher(BlockPos p_155229_, BlockState p_155230_) {
        super(Tiles.washer.get(), p_155229_, p_155230_);

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
        
        items = new ItemStackHandler(13) {          
            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack)
            {
                return stack.is(Items.COBBLESTONE) && slot < 3;
            }
            
            @Override
            public void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            @NotNull
            public ItemStack extractItem(int slot, int amount, boolean simulate)
            {
                if (slot < 3) return ItemStack.EMPTY;
                return super.extractItem(slot, amount, simulate);
            }
        };

        lazyTank = LazyOptional.of(() -> tank);
        lazyBattery = LazyOptional.of(() -> battery);
        lazyItems = LazyOptional.of(() -> items);
    }

    
    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity be) {
        if (level.isClientSide) return;
        TileWasher washer = (TileWasher) be;


        if (!washer.ticked) {
            washer.tank.setCapacity(FutureGeneratorsConfig.SERVER.washerWaterCapacity.get());
    
            washer.battery.setCapacity(FutureGeneratorsConfig.SERVER.washerPowerCapacity.get());

            washer.ticked = true;
        }


        washer.ticks++;
        if (washer.canProcess() && (washer.ticks % FutureGeneratorsConfig.SERVER.washerProcessTime.get() == 0)) {
            for(int i=0;i<3;i++) {
                if (washer.items.getStackInSlot(i).isEmpty()) continue;
                if (!washer.canProcess()) return; //we're either out of water or completely out of cobble. just skip
                washer.items.getStackInSlot(i).setCount(washer.items.getStackInSlot(i).getCount() - 1);           
                
                washer.tank.drain(FutureGeneratorsConfig.SERVER.washerWaterConsumption.get(), FluidAction.EXECUTE);
                washer.battery.setEnergy(washer.battery.getEnergyStored() - FutureGeneratorsConfig.SERVER.washerPowerConsumption.get());
    
                for (ItemStack stack : drops.keySet()) {
                    float f = drops.get(stack);
                    float c = level.random.nextFloat();
                    if (f >= c) {
                        ItemStack insert = stack.copy();
    
                        for (int j=3;j<washer.items.getSlots();j++) {
                            ItemStack slot = washer.items.getStackInSlot(j);
                            if (slot.isEmpty()) {
                                washer.items.setStackInSlot(j, insert);
                                break;
                            }
                            else {
                                if (ItemStack.isSameItem(insert, slot) && slot.getCount() < 64) {
                                    slot.setCount(slot.getCount() + 1);
                                    break;
                                } 
                            }
                        }
    
                    }
                }
            }

        }

    }
    
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("tank", tank.writeToNBT(new CompoundTag()));
        tag.put("battery", battery.serializeNBT());
        tag.put("items", items.serializeNBT());

    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("tank")) {
            tank.readFromNBT(tag.getCompound("tank"));
        }
        if (!tank.getFluid().getFluid().isSame(Fluids.HOT_WATER.get())) {
            //convert water into hot water
            tank.setFluid(new FluidStack(Fluids.HOT_WATER.get(), tank.getFluidAmount()));
        }
        if (tag.contains("battery")) {
            battery.deserializeNBT(tag.get("battery"));
        }

        if (tag.contains("items")) {
            items.deserializeNBT(tag.getCompound("items"));
        }
    }

    public boolean canProcess() {
        return battery.getEnergyStored() >= FutureGeneratorsConfig.SERVER.washerPowerConsumption.get() &&
            tank.getFluidAmount() >= FutureGeneratorsConfig.SERVER.washerWaterConsumption.get() &&
            (
                items.getStackInSlot(0).getCount() > 0 ||
                items.getStackInSlot(1).getCount() > 0 ||
                items.getStackInSlot(2).getCount() > 0
            );
    }
    
}
