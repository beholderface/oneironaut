package org.arcticquests.dev.oneironaut.oneironautt.block;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.CellEntity;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CellBlock extends BaseEntityBlock {
    public CellBlock(Properties settings){
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CellEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return !(world.isClientSide) ? (_world, _pos, _state, _be) -> ((CellEntity)_be).tick(_world, _pos, _state) : null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state){
        return RenderShape.MODEL;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                                LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        Optional<CellEntity> be = world.getBlockEntity(pos, OneironautBlockRegistry.CELL_ENTITY.get());
        if (be.isPresent()){
            be.get().updateNeighborMap();
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }
}
