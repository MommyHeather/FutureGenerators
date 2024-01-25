package co.uk.mommyheather.futuregenerators.ui;

import co.uk.mommyheather.futuregenerators.blocks.Blocks;
import co.uk.mommyheather.futuregenerators.tile.TileFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

public class FluidTankMenu extends FutureGeneratorsMenu {

    public TileFluidTank tank;

    public FluidTankMenu(int windowId, Player player, BlockPos pos) {
        super(Menus.fluidTank.get(), windowId, player, pos, Blocks.fluidTank.get());

        BlockEntity be = player.level().getBlockEntity(pos);

        if (be instanceof TileFluidTank) {
            tank = (TileFluidTank) be;
        }
        layoutPlayerInventorySlots(player.getInventory(), 8, 84);

        
    }
    
}
