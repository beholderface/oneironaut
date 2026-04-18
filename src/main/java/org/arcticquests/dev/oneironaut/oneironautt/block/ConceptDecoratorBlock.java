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
import org.jetbrains.annotations.Nullable;

//it's the same as ConceptModifierBlock but strictly decorative
public class ConceptDecoratorBlock extends Block {
    public ConceptDecoratorBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.FACING);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, ctx.getClickedFace());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context){
        return switch (state.getValue(BlockStateProperties.FACING)){
            case DOWN -> Shapes.box(3.0 / 16, 2.0 / 16, 3.0 / 16, 13.0 / 16, 16.0 / 16, 13.0 / 16);
            case UP -> Shapes.box(3.0 / 16, 0.0 / 16, 3.0 / 16, 13.0 / 16, 14.0 / 16, 13.0 / 16);
            case NORTH -> Shapes.box(3.0 / 16, 3.0 / 16, 2.0 / 16, 13.0 / 16, 13.0 / 16, 16.0 / 16);
            case SOUTH -> Shapes.box(3.0 / 16, 3.0 / 16, 0.0 / 16, 13.0 / 16, 13.0 / 16, 14.0 / 16);
            case WEST -> Shapes.box(2.0 / 16, 3.0 / 16, 3.0 / 16, 16.0 / 16, 13.0 / 16, 13.0 / 16);
            case EAST -> Shapes.box(0.0 / 16, 3.0 / 16, 3.0 / 16, 14.0 / 16, 13.0 / 16, 13.0 / 16);
        };
    }
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context){
        return switch (state.getValue(BlockStateProperties.FACING)){
            case DOWN -> Shapes.box(3.0 / 16, 2.0 / 16, 3.0 / 16, 13.0 / 16, 16.0 / 16, 13.0 / 16);
            case UP -> Shapes.box(3.0 / 16, 0.0 / 16, 3.0 / 16, 13.0 / 16, 14.0 / 16, 13.0 / 16);
            case NORTH -> Shapes.box(3.0 / 16, 3.0 / 16, 2.0 / 16, 13.0 / 16, 13.0 / 16, 16.0 / 16);
            case SOUTH -> Shapes.box(3.0 / 16, 3.0 / 16, 0.0 / 16, 13.0 / 16, 13.0 / 16, 14.0 / 16);
            case WEST -> Shapes.box(2.0 / 16, 3.0 / 16, 3.0 / 16, 16.0 / 16, 13.0 / 16, 13.0 / 16);
            case EAST -> Shapes.box(0.0 / 16, 3.0 / 16, 3.0 / 16, 14.0 / 16, 13.0 / 16, 13.0 / 16);
        };
    }
}
