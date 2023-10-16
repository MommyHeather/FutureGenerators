package co.uk.mommyheather.futuregenerators.ui;

import co.uk.mommyheather.futuregenerators.blocks.Blocks;
import co.uk.mommyheather.futuregenerators.config.FutureGeneratorsConfig;
import co.uk.mommyheather.futuregenerators.tile.TileWasher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class WasherMenu extends FutureGeneratorsMenu {

    public int water;
    public int waterMax;
    public int power;
    public int powerMax;

    public WasherMenu(int windowId, Player player, BlockPos pos) {
        super(Menus.washer.get(), windowId, player, pos, Blocks.washer.get());

        BlockEntity be = player.level().getBlockEntity(pos);

        SLOT_COUNT = 13;
        SLOT_INPUT = 3;

        

        if (be instanceof TileWasher) {
            TileWasher washer = (TileWasher) be;


            addSlot(new SlotItemHandler(washer.items, 0, 62, 15));
            addSlot(new SlotItemHandler(washer.items, 1, 80, 15));
            addSlot(new SlotItemHandler(washer.items, 2, 98, 15));

            
            addSlot(new SlotItemHandler(washer.items, 3, 44, 44));
            addSlot(new SlotItemHandler(washer.items, 4, 62, 44));
            addSlot(new SlotItemHandler(washer.items, 5, 80, 44));
            addSlot(new SlotItemHandler(washer.items, 6, 98, 44));
            addSlot(new SlotItemHandler(washer.items, 7, 116, 44));
            
            addSlot(new SlotItemHandler(washer.items, 8, 44, 62));
            addSlot(new SlotItemHandler(washer.items, 9, 62, 62));
            addSlot(new SlotItemHandler(washer.items, 10, 80, 62));
            addSlot(new SlotItemHandler(washer.items, 11, 98, 62));
            addSlot(new SlotItemHandler(washer.items, 12, 116, 62));

            
            layoutPlayerInventorySlots(player.getInventory(), 8, 84);

            //power - needs to be bitshifted as dataslots are short capped
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return washer.battery.getEnergyStored() & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    WasherMenu.this.power = (WasherMenu.this.power & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (washer.battery.getEnergyStored() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    WasherMenu.this.power = (WasherMenu.this.power & 0xffff) | ((pValue & 0xffff) << 16);
                }
            });

            //water - needs to be bitshifted as dataslots are short capped
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return washer.tank.getFluidAmount() & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    WasherMenu.this.water = (WasherMenu.this.water & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (washer.tank.getFluidAmount() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    WasherMenu.this.water = (WasherMenu.this.water & 0xffff) | ((pValue & 0xffff) << 16);
                }
            });

            //powerMax - needs to be bitshifted as dataslots are short capped
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return washer.battery.getMaxEnergyStored() & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    WasherMenu.this.powerMax = (WasherMenu.this.powerMax & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (washer.battery.getMaxEnergyStored() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    WasherMenu.this.powerMax = (WasherMenu.this.powerMax & 0xffff) | ((pValue & 0xffff) << 16);
                }
            });

            //waterMax - needs to be bitshifted as dataslots are short capped
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return washer.tank.getCapacity() & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    WasherMenu.this.waterMax = (WasherMenu.this.waterMax & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (washer.tank.getCapacity() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    WasherMenu.this.waterMax = (WasherMenu.this.waterMax & 0xffff) | ((pValue & 0xffff) << 16);
                }
            });


        }

        
    }
    
}
