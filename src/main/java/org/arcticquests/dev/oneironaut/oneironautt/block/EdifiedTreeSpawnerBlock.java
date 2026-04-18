package org.arcticquests.dev.oneironaut.oneironautt.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.EdifiedTreeSpawnerBlockEntity;
import org.jetbrains.annotations.Nullable;

public class EdifiedTreeSpawnerBlock extends BaseEntityBlock {
    public EdifiedTreeSpawnerBlock(Properties settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EdifiedTreeSpawnerBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state){
        return RenderShape.INVISIBLE;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return (_world, _pos, _state, _be) -> ((EdifiedTreeSpawnerBlockEntity)_be).tick(_world, _pos, _state);
    }
}
