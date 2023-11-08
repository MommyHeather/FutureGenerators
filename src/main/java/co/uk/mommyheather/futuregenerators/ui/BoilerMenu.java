package co.uk.mommyheather.futuregenerators.ui;

import co.uk.mommyheather.futuregenerators.blocks.Blocks;
import co.uk.mommyheather.futuregenerators.config.FutureGeneratorsConfig;
import co.uk.mommyheather.futuregenerators.tile.TileBoiler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class BoilerMenu extends FutureGeneratorsMenu {

    public int timeRemaining; //so this is seconds
    public int water;
    public int hotWater;
    public int waterMax;

    public BoilerMenu(int windowId, Player player, BlockPos pos) {
        super(Menus.boiler.get(), windowId, player, pos, Blocks.boiler.get());

        BlockEntity be = player.level().getBlockEntity(pos);

        SLOT_COUNT = 1;
        SLOT_INPUT = 1;
        

        if (be instanceof TileBoiler) {
            TileBoiler boiler = (TileBoiler) be;
            
            addSlot(new SlotItemHandler(boiler.items, 0, 80, 53));
            
            layoutPlayerInventorySlots(player.getInventory(), 8, 84);

            //time remaining - we'll go divide by 20 so we count in seconds rather than changing the value every tick
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (boiler.fuelRemaining / 20) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    BoilerMenu.this.timeRemaining = (BoilerMenu.this.timeRemaining & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return ((boiler.fuelRemaining / 20) >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    BoilerMenu.this.timeRemaining = (BoilerMenu.this.timeRemaining & 0xffff) | ((pValue & 0xffff) << 16);
                }
            });

            //cold water - needs to be bitshifted as dataslots are short capped
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return boiler.tank.getFluidInTank(0).getAmount() & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    BoilerMenu.this.water = (BoilerMenu.this.water & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (boiler.tank.getFluidInTank(0).getAmount() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    BoilerMenu.this.water = (BoilerMenu.this.water & 0xffff) | ((pValue & 0xffff) << 16);
                }
            });

            //hot water - needs to be bitshifted as dataslots are short capped
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return boiler.tank.getFluidInTank(1).getAmount() & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    BoilerMenu.this.hotWater = (BoilerMenu.this.hotWater & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (boiler.tank.getFluidInTank(1).getAmount() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    BoilerMenu.this.hotWater = (BoilerMenu.this.hotWater & 0xffff) | ((pValue & 0xffff) << 16);
                }
            });

            //and we'll store max cold water, no point sending over max hot though
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return FutureGeneratorsConfig.SERVER.boilerWaterCapacity.get() & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    BoilerMenu.this.waterMax = (BoilerMenu.this.waterMax & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (FutureGeneratorsConfig.SERVER.boilerWaterCapacity.get() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    BoilerMenu.this.waterMax = (BoilerMenu.this.waterMax & 0xffff) | ((pValue & 0xffff) << 16);
                }
            });
        }

        
    }
    
}
