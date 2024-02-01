package co.uk.mommyheather.futuregenerators.ui;

import co.uk.mommyheather.futuregenerators.blocks.Blocks;
import co.uk.mommyheather.futuregenerators.tile.TilePump;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;

public class PumpMenu extends FutureGeneratorsMenu {

    public TilePump pump;

    public PumpMenu(int windowId, Player player, BlockPos pos) {
        super(Menus.pump.get(), windowId, player, pos, Blocks.pump.get());

        BlockEntity be = player.level().getBlockEntity(pos);

        if (be instanceof TilePump) {
            pump = (TilePump) be;

            addSlot(new SlotItemHandler(pump.items, 0, 35, 38));
        }
        layoutPlayerInventorySlots(player.getInventory(), 8, 84);

        
    }
    
}
