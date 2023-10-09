package co.uk.mommyheather.futuregenerators.blocks;

import javax.annotation.Nullable;

import co.uk.mommyheather.futuregenerators.tile.TileTurbine;
import co.uk.mommyheather.futuregenerators.tile.Tiles;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockTurbine extends Block implements EntityBlock {

    public BlockTurbine(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileTurbine(pos, state);
    }
    
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == Tiles.turbine.get() ? TileTurbine::tick : null;
    }

}
