package co.uk.mommyheather.futuregenerators.blocks;

import javax.annotation.Nullable;

import co.uk.mommyheather.futuregenerators.tile.TileFluidTank;
import co.uk.mommyheather.futuregenerators.tile.TilePump;
import co.uk.mommyheather.futuregenerators.tile.Tiles;
import co.uk.mommyheather.futuregenerators.ui.FluidTankMenu;
import co.uk.mommyheather.futuregenerators.ui.TurbineMenu;
import co.uk.mommyheather.futuregenerators.util.TransferUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class BlockPump extends Block implements EntityBlock {
    
    public BlockPump(Properties p_49795_) {
        super(p_49795_);
    }
    
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TilePump(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == Tiles.pump.get() ? TilePump::tick : null;
    }

    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof TilePump) {
                if (!player.isShiftKeyDown()) {
                    ItemStack stack = player.getItemInHand(hand);
                    if (!stack.isEmpty() && TransferUtils.handleFluidInteraction(player, hand, stack, ((TilePump) be).tank)) {
                        player.getInventory().setChanged();
                        return InteractionResult.SUCCESS;
                    }
                }
                /*MenuProvider containerProvider = new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable("futuregenerators.ui.pump");
                    }
                    
                    @Override
                    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                        return new PumpMenu(windowId, playerEntity, pos);
                    }
                };
                NetworkHooks.openScreen((ServerPlayer) player, containerProvider, be.getBlockPos());*/
            }
        }
        return InteractionResult.SUCCESS;
    }
    
    @Override     
    public VoxelShape getVisualShape(BlockState p_48735_, BlockGetter p_48736_, BlockPos p_48737_, CollisionContext p_48738_) {
        return super.getVisualShape(p_48735_, p_48736_, p_48737_, p_48738_);
    }
    
}
