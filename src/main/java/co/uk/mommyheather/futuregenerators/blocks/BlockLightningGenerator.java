package co.uk.mommyheather.futuregenerators.blocks;

import java.util.Optional;

import javax.annotation.Nullable;

import co.uk.mommyheather.futuregenerators.tile.TileLightningGenerator;
import co.uk.mommyheather.futuregenerators.tile.Tiles;
import co.uk.mommyheather.futuregenerators.ui.LightningGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public class BlockLightningGenerator extends Block implements EntityBlock {
    
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public BlockLightningGenerator(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileLightningGenerator(pos, state);
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == Tiles.lightningGenerator.get() ? TileLightningGenerator::tick : null;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) {
        if (!level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof TileLightningGenerator) {
                MenuProvider containerProvider = new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable("futuregenerators.ui.lightning_generator");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                        return new LightningGeneratorMenu(windowId, playerEntity, pos);
                    }
                };
                NetworkHooks.openScreen((ServerPlayer) player, containerProvider, be.getBlockPos());
            }
        }
        return InteractionResult.SUCCESS;
    }


    @Deprecated
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos p_60513_, boolean p_60514_) {
        if (level.isClientSide) return;

        Optional<TileLightningGenerator> generatorOptional = level.getBlockEntity(pos, Tiles.lightningGenerator.get());

        if (generatorOptional.isPresent()) {
            TileLightningGenerator generator = generatorOptional.get();
            generator.checkNeighbours();
        }
    }


    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean p_51542_) {
        if (!state.is(oldState.getBlock())) {
            TileLightningGenerator generator = (TileLightningGenerator) level.getBlockEntity(pos);
            for (int i=0; i<generator.items.getSlots();i++) {
                if (!generator.items.getStackInSlot(i).isEmpty()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), generator.items.getStackInSlot(i));
                }
            }
        }
        super.onRemove(state, level, pos, oldState, p_51542_);
    }
}
