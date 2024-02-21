package co.uk.mommyheather.futuregenerators.ui;

import co.uk.mommyheather.futuregenerators.blocks.Blocks;
import co.uk.mommyheather.futuregenerators.config.FutureGeneratorsConfig;
import co.uk.mommyheather.futuregenerators.tile.TileTurbineController;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MultiBlockTurbineMenu extends FutureGeneratorsMenu {

    public int water;
    public int waterMax;
    public int power;
    public int powerMax;
    public int speed;
    public int speedMax;

    public MultiBlockTurbineMenu(int windowId, Player player, BlockPos pos) {
        super(Menus.multiblockTurbine.get(), windowId, player, pos, Blocks.turbineController.get());

        BlockEntity be = player.level().getBlockEntity(pos);
        
        layoutPlayerInventorySlots(player.getInventory(), 8, 84);

        if (be instanceof TileTurbineController) {
            TileTurbineController turbine = (TileTurbineController) be;
            //power - needs to be bitshifted as dataslots are short capped
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return turbine.battery.getEnergyStored() & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    MultiBlockTurbineMenu.this.power = (MultiBlockTurbineMenu.this.power & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (turbine.battery.getEnergyStored() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    MultiBlockTurbineMenu.this.power = (MultiBlockTurbineMenu.this.power & 0xffff) | ((pValue & 0xffff) << 16);
                }
            });

            //water - needs to be bitshifted as dataslots are short capped
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return turbine.tank.getFluidAmount() & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    MultiBlockTurbineMenu.this.water = (MultiBlockTurbineMenu.this.water & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (turbine.tank.getFluidAmount() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    MultiBlockTurbineMenu.this.water = (MultiBlockTurbineMenu.this.water & 0xffff) | ((pValue & 0xffff) << 16);
                }
            });

            //powerMax - needs to be bitshifted as dataslots are short capped
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return turbine.battery.getMaxEnergyStored() & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    MultiBlockTurbineMenu.this.powerMax = (MultiBlockTurbineMenu.this.powerMax & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (turbine.battery.getMaxEnergyStored() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    MultiBlockTurbineMenu.this.powerMax = (MultiBlockTurbineMenu.this.powerMax & 0xffff) | ((pValue & 0xffff) << 16);
                }
            });

            //waterMax - needs to be bitshifted as dataslots are short capped
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return turbine.tank.getCapacity() & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    MultiBlockTurbineMenu.this.waterMax = (MultiBlockTurbineMenu.this.waterMax & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (turbine.tank.getCapacity() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    MultiBlockTurbineMenu.this.waterMax = (MultiBlockTurbineMenu.this.waterMax & 0xffff) | ((pValue & 0xffff) << 16);
                }
            });


            

            //speed - needs to be bitshifted as dataslots are short capped
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return turbine.speed & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    MultiBlockTurbineMenu.this.speed = (MultiBlockTurbineMenu.this.speed & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (turbine.speed >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    MultiBlockTurbineMenu.this.speed = (MultiBlockTurbineMenu.this.speed & 0xffff) | ((pValue & 0xffff) << 16);
                }
            });

            //speedMax - needs to be bitshifted as dataslots are short capped
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return FutureGeneratorsConfig.SERVER.multiblockTurbineMaxSpeed.get() & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    MultiBlockTurbineMenu.this.speedMax = (MultiBlockTurbineMenu.this.speedMax & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (FutureGeneratorsConfig.SERVER.multiblockTurbineMaxSpeed.get() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    MultiBlockTurbineMenu.this.speedMax = (MultiBlockTurbineMenu.this.speedMax & 0xffff) | ((pValue & 0xffff) << 16);
                }
            });

        }

        
    }
    
}
