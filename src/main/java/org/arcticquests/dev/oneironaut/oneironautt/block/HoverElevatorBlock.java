package org.arcticquests.dev.oneironaut.oneironautt.block;


import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.HoverElevatorBlockEntity;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class HoverElevatorBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public HoverElevatorBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState().setValue(POWERED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
        builder.add(POWERED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HoverElevatorBlockEntity(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState output = this.defaultBlockState().setValue(POWERED, pContext.getLevel().hasNeighborSignal(pContext.getClickedPos()));
        if (pContext.getPlayer() != null){
            return output.setValue(BlockStateProperties.FACING, !pContext.getPlayer().isShiftKeyDown() ? pContext.getClickedFace() : pContext.getClickedFace().getOpposite());
        } else {
            return output.setValue(BlockStateProperties.FACING, pContext.getClickedFace());
        }
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return (_world, _pos, _state, _be) -> ((HoverElevatorBlockEntity)_be).tick(_world, _pos, _state);
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos,
                               boolean pIsMoving) {
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
        if (!pLevel.isClientSide) {
            boolean currentlyPowered = pState.getValue(POWERED);
            if (currentlyPowered != pLevel.hasNeighborSignal(pPos)) {
                pLevel.setBlock(pPos, pState.setValue(POWERED, !currentlyPowered), 2);
            }
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState pState) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        int output = 0;
        Optional<HoverElevatorBlockEntity> blockEntityMaybe = world.getBlockEntity(pos, OneironautBlockRegistry.HOVER_ELEVATOR_ENTITY.get());
        if (blockEntityMaybe.isPresent()){
            output = blockEntityMaybe.get().getBlockState().getAnalogOutputSignal(world, pos);
        }
        return output;
    }

    @Override
    public RenderShape getRenderShape(BlockState state){
        return RenderShape.MODEL;
    }
}
