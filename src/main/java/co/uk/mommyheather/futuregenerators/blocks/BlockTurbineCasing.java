package co.uk.mommyheather.futuregenerators.blocks;

import javax.annotation.Nullable;

import co.uk.mommyheather.futuregenerators.tile.TileTurbineCasing;
import co.uk.mommyheather.futuregenerators.tile.TileTurbineController;
import co.uk.mommyheather.futuregenerators.tile.Tiles;
import co.uk.mommyheather.futuregenerators.ui.MultiBlockTurbineMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class BlockTurbineCasing extends Block implements EntityBlock {
    

    public BlockTurbineCasing(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileTurbineCasing(pos, state);
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == Tiles.turbineCasing.get() ? TileTurbineCasing::tick : null;
    }
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean p_51542_) {
        if (!state.is(oldState.getBlock())) {
            TileTurbineCasing casing = (TileTurbineCasing) level.getBlockEntity(pos);
            if (casing.controller != null) casing.controller.valid = false;
        }
        super.onRemove(state, level, pos, oldState, p_51542_);
    }
/* 
//Disabled on request - only the controller should open the ui.
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof TileTurbineCasing casing) {
                if (casing.controller == null) {
                    player.sendSystemMessage(Component.translatable("futuregenerators.multiblock.casing_no_controller"));
                    return InteractionResult.FAIL;
                } 
                MenuProvider containerProvider = new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable("futuregenerators.ui.turbine");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                        return new MultiBlockTurbineMenu(windowId, playerEntity, casing.controller.getBlockPos());
                    }
                };
                NetworkHooks.openScreen((ServerPlayer) player, containerProvider, casing.controller.getBlockPos());
            }
        }
        return InteractionResult.SUCCESS;
    }
    */

}
