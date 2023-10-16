package co.uk.mommyheather.futuregenerators.ui;

import co.uk.mommyheather.futuregenerators.blocks.Blocks;
import co.uk.mommyheather.futuregenerators.tile.TileLightningDynamo;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.entity.BlockEntity;

public class LightningDynamoMenu extends FutureGeneratorsMenu {

    public int power;
    public int powerMax;
    public int generators;

    public LightningDynamoMenu(int windowId, Player player, BlockPos pos) {
        super(Menus.lightningDynamo.get(), windowId, player, pos, Blocks.lightningDynamo.get());

        BlockEntity be = player.level().getBlockEntity(pos);

        if (be instanceof TileLightningDynamo) {
            TileLightningDynamo dynamo = (TileLightningDynamo) be;


            
            layoutPlayerInventorySlots(player.getInventory(), 8, 84);

            //power - needs to be bitshifted as dataslots are short capped
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return dynamo.battery.getEnergyStored() & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    LightningDynamoMenu.this.power = (LightningDynamoMenu.this.power & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (dynamo.battery.getEnergyStored() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    LightningDynamoMenu.this.power = (LightningDynamoMenu.this.power & 0xffff) | ((pValue & 0xffff) << 16);
                }
            });

            

            //powerMax - needs to be bitshifted as dataslots are short capped
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return dynamo.battery.getMaxEnergyStored() & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    LightningDynamoMenu.this.powerMax = (LightningDynamoMenu.this.powerMax & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (dynamo.battery.getMaxEnergyStored() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    LightningDynamoMenu.this.powerMax = (LightningDynamoMenu.this.powerMax & 0xffff) | ((pValue & 0xffff) << 16);
                }
            });

            
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    int i = 0;
                    for (Direction direction : Direction.values()) {
                        if (dynamo.generators.get(direction) != null) i++; 
                    }
                    return i;
                }

                @Override
                public void set(int pValue) {
                    LightningDynamoMenu.this.generators = pValue;
                }
            });

        }

        
    }
    
}
