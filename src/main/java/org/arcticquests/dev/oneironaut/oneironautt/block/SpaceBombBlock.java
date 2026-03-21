package org.arcticquests.dev.oneironaut.oneironautt.block;

import at.petrak.hexcasting.common.blocks.circles.BlockSlate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.SpaceBombBlockEntity;
import org.jetbrains.annotations.Nullable;

public class SpaceBombBlock extends BaseEntityBlock {

    public SpaceBombBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(BlockSlate.ENERGIZED, false));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpaceBombBlockEntity(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockSlate.ENERGIZED);
    }

    @Override
    public RenderShape getRenderShape(BlockState state){
        return RenderShape.MODEL;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return (_world, _pos, _state, _be) -> ((SpaceBombBlockEntity)_be).tick(_world, _pos, _state);
    }
}
