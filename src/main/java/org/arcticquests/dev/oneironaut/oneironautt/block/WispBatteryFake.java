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
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.WispBatteryEntityFake;
import org.jetbrains.annotations.Nullable;

public class WispBatteryFake extends BaseEntityBlock {
    public static final BooleanProperty REDSTONE_POWERED = BlockStateProperties.POWERED;
    public WispBatteryFake(Properties settings){
        super(settings);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(REDSTONE_POWERED, false));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(REDSTONE_POWERED);
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

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return (_world, _pos, _state, _be) -> ((WispBatteryEntityFake)_be).tick(_world, _pos, _state);
    }

    //it doesn't actually, I just want redstone to point at it
    public boolean isSignalSource(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new WispBatteryEntityFake(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(REDSTONE_POWERED, pContext.getLevel().hasNeighborSignal(pContext.getClickedPos()));
    }
}
