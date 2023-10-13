package co.uk.mommyheather.futuregenerators.ui;

import co.uk.mommyheather.futuregenerators.blocks.Blocks;
import co.uk.mommyheather.futuregenerators.config.FutureGeneratorsConfig;
import co.uk.mommyheather.futuregenerators.tile.TileTurbine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.block.entity.BlockEntity;

public class TurbineMenu extends FutureGeneratorsMenu {

    public int water;
    public int waterMax;
    public int power;
    public int powerMax;
    public int speed;
    public int speedMax;

    public TurbineMenu(int windowId, Player player, BlockPos pos) {
        super(Menus.turbine.get(), windowId, player, pos, Blocks.turbine.get());

        BlockEntity be = player.level().getBlockEntity(pos);
        
        layoutPlayerInventorySlots(player.getInventory(), 8, 96);

        if (be instanceof TileTurbine) {
            TileTurbine turbine = (TileTurbine) be;
            //power - needs to be bitshifted as dataslots are short capped
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return turbine.battery.getEnergyStored() & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    TurbineMenu.this.power = (TurbineMenu.this.power & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (turbine.battery.getEnergyStored() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    TurbineMenu.this.power = (TurbineMenu.this.power & 0xffff) | ((pValue & 0xffff) << 16);
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
                    TurbineMenu.this.water = (TurbineMenu.this.water & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (turbine.tank.getFluidAmount() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    TurbineMenu.this.water = (TurbineMenu.this.water & 0xffff) | ((pValue & 0xffff) << 16);
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
                    TurbineMenu.this.powerMax = (TurbineMenu.this.powerMax & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (turbine.battery.getMaxEnergyStored() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    TurbineMenu.this.powerMax = (TurbineMenu.this.powerMax & 0xffff) | ((pValue & 0xffff) << 16);
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
                    TurbineMenu.this.waterMax = (TurbineMenu.this.waterMax & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (turbine.tank.getCapacity() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    TurbineMenu.this.waterMax = (TurbineMenu.this.waterMax & 0xffff) | ((pValue & 0xffff) << 16);
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
                    TurbineMenu.this.speed = (TurbineMenu.this.speed & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (turbine.speed >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    TurbineMenu.this.speed = (TurbineMenu.this.speed & 0xffff) | ((pValue & 0xffff) << 16);
                }
            });

            //speedMax - needs to be bitshifted as dataslots are short capped
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return FutureGeneratorsConfig.SERVER.turbineMaxSpeed.get() & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    TurbineMenu.this.speedMax = (TurbineMenu.this.speedMax & 0xffff0000) | (pValue & 0xffff);
                }
            });
            addDataSlot(new DataSlot() {
                @Override
                public int get() {
                    return (FutureGeneratorsConfig.SERVER.turbineMaxSpeed.get() >> 16) & 0xffff;
                }

                @Override
                public void set(int pValue) {
                    TurbineMenu.this.speedMax = (TurbineMenu.this.speedMax & 0xffff) | ((pValue & 0xffff) << 16);
                }
            });

        }

        
    }
    
}
