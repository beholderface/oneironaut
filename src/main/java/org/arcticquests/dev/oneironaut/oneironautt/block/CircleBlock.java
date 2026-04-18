package org.arcticquests.dev.oneironaut.oneironautt.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CircleBlock extends Block {

    public CircleBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, pContext.getClickedFace());
    }
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context){
        return switch (state.getValue(BlockStateProperties.FACING)){
            case DOWN -> Shapes.box(0.0 / 16, 15.0 / 16, 0.0 / 16, 16.0 / 16, 16.0 / 16, 16.0 / 16);
            case UP -> Shapes.box(0.0 / 16, 0.0 / 16, 0.0 / 16, 16.0 / 16, 1.0 / 16, 16.0 / 16);
            case NORTH -> Shapes.box(0.0 / 16, 0.0 / 16, 15.0 / 16, 16.0 / 16, 16.0 / 16, 16.0 / 16);
            case SOUTH -> Shapes.box(0.0 / 16, 0.0 / 16, 0.0 / 16, 16.0 / 16, 16.0 / 16, 1.0 / 16);
            case WEST -> Shapes.box(15.0 / 16, 0.0 / 16, 0.0 / 16, 16.0 / 16, 16.0 / 16, 16.0 / 16);
            case EAST -> Shapes.box(0.0 / 16, 0.0 / 16, 0.0 / 16, 1.0 / 16, 16.0 / 16, 16.0 / 16);
        };
    }
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context){
        return switch (state.getValue(BlockStateProperties.FACING)){
            case DOWN -> Shapes.box(0.0 / 16, 15.9 / 16, 0.0 / 16, 16.0 / 16, 16.0 / 16, 16.0 / 16);
            case UP -> Shapes.box(0.0 / 16, 0.0 / 16, 0.0 / 16, 16.0 / 16, 0.1 / 16, 16.0 / 16);
            case NORTH -> Shapes.box(0.0 / 16, 0.0 / 16, 15.9 / 16, 16.0 / 16, 16.0 / 16, 16.0 / 16);
            case SOUTH -> Shapes.box(0.0 / 16, 0.0 / 16, 0.0 / 16, 16.0 / 16, 16.0 / 16, 0.1 / 16);
            case WEST -> Shapes.box(15.9 / 16, 0.0 / 16, 0.0 / 16, 16.0 / 16, 16.0 / 16, 16.0 / 16);
            case EAST -> Shapes.box(0.0 / 16, 0.0 / 16, 0.0 / 16, 0.1 / 16, 16.0 / 16, 16.0 / 16);
        };
    }
}
