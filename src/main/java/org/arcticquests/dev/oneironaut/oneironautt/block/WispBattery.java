package org.arcticquests.dev.oneironaut.oneironautt.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.WispBatteryEntity;
import org.jetbrains.annotations.Nullable;

public class WispBattery extends BaseEntityBlock {
    public static final BooleanProperty REDSTONE_POWERED = BlockStateProperties.POWERED;
    public WispBattery(Properties settings){
        super(settings);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(REDSTONE_POWERED, false));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(REDSTONE_POWERED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WispBatteryEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return (_world, _pos, _state, _be) -> ((WispBatteryEntity)_be).tick(_world, _pos, _state);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        long media = ((WispBatteryEntity)world.getBlockEntity(pos)).getMedia();
        long capacity = WispBatteryEntity.CAPACITY;
        if (media < 0){
            return (int) ((world.getGameTime() / 2) % 15);
        } else {
            return (int) Math.floor(((double) media / capacity) * 15);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state){
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context){
        VoxelShape lowerHalf = Shapes.box(0.0, 0.0, 0.0, 1.0, 8.0 / 16.0, 1.0);
        VoxelShape phialBorder = Shapes.box(3.0 / 16, 8.0 / 16, 3.0 / 16, 13.0  / 16, 10.0 / 16, 13.0 / 16);
        VoxelShape phialUpper = Shapes.box(5.0/16, 8.0/16, 5.0/16, 11.0/16, 11.0/16, 11.0/16);
        VoxelShape antenna = Shapes.box(7.0/16, 8.0/16, 7.0/16, 9.0/16, 1.0, 9.0/16);
        return Shapes.or(lowerHalf, phialBorder, phialUpper, antenna);
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos,
                               boolean pIsMoving) {
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);

        if (!pLevel.isClientSide) {
            boolean currentlyPowered = pState.getValue(REDSTONE_POWERED);
            if (currentlyPowered != pLevel.hasNeighborSignal(pPos)) {
                pLevel.setBlock(pPos, pState.setValue(REDSTONE_POWERED, !currentlyPowered), 2);
            }
        }
    }

    //it doesn't actually, I just want redstone to point at it
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(REDSTONE_POWERED, pContext.getLevel().hasNeighborSignal(pContext.getClickedPos()));
    }
}
