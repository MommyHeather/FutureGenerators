package co.uk.mommyheather.futuregenerators.ui;

import co.uk.mommyheather.futuregenerators.blocks.Blocks;
import co.uk.mommyheather.futuregenerators.tile.TileLightningGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class LightningGeneratorMenu extends FutureGeneratorsMenu {

    public int power;
    public int powerMax;
    public int dynamos;
    public boolean hasRod;

    public LightningGeneratorMenu(int windowId, Player player, BlockPos pos) {
        super(Menus.lightningGenerator.get(), windowId, player, pos, Blocks.lightningGenerator.get());

        BlockEntity be = player.level().getBlockEntity(pos);

        if (be instanceof TileLightningGenerator) {
            TileLightningGenerator generator = (TileLightningGenerator) be;
            SLOT_COUNT = 6;
            SLOT_INPUT = 6;

            addSlot(new SlotItemHandler(generator.items, 0, 100, 25));
            addSlot(new SlotItemHandler(generator.items, 1, 118, 25));
            addSlot(new SlotItemHandler(generator.items, 2, 100, 43));
            addSlot(new SlotItemHandler(generator.items, 3, 118, 43));
            addSlot(new SlotItemHandler(generator.items, 4, 100, 60));
            addSlot(new SlotItemHandler(generator.items, 5, 118, 60));


            
            layoutPlayerInventorySlots(player.getInventory(), 8, 96);

            //power - needs to be bitshifted as dataslots are short capped
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return generator.battery.getEnergyStored() & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    LightningGeneratorMenu.this.power = (LightningGeneratorMenu.this.power & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (generator.battery.getEnergyStored() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    LightningGeneratorMenu.this.power = (LightningGeneratorMenu.this.power & 0xffff) | ((pValue & 0xffff) << 16);
                }
            });

            

            //powerMax - needs to be bitshifted as dataslots are short capped
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return generator.battery.getMaxEnergyStored() & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    LightningGeneratorMenu.this.powerMax = (LightningGeneratorMenu.this.powerMax & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (generator.battery.getMaxEnergyStored() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    LightningGeneratorMenu.this.powerMax = (LightningGeneratorMenu.this.powerMax & 0xffff) | ((pValue & 0xffff) << 16);
                }
            });

            
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return generator.hasRod ? 1 : 0;
                }

                @Override
                public void set(int pValue) {
                    LightningGeneratorMenu.this.hasRod = (pValue == 1);
                }
            });

            
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return generator.dynamos;
                }

                @Override
                public void set(int pValue) {
                    LightningGeneratorMenu.this.dynamos = pValue;
                }
            });

        }

        
    }
    
}
