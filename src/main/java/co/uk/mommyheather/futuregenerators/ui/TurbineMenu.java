package co.uk.mommyheather.futuregenerators.ui;

import co.uk.mommyheather.futuregenerators.blocks.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public class TurbineMenu extends FutureGeneratorsMenu {

    public TurbineMenu(int windowId, Player player, BlockPos pos) {
        super(Menus.turbine.get(), windowId, player, pos, Blocks.turbine.get());
    }
    
}
