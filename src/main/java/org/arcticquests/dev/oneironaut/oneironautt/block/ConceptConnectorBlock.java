package org.arcticquests.dev.oneironaut.oneironautt.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class ConceptConnectorBlock extends Block implements IConceptSocketed {
    public ConceptConnectorBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.FACING);
    }

    @Override
    public EnumSet<Direction> getSockets(BlockState state) {
        return switch (state.getValue(BlockStateProperties.FACING).getAxis()){
            case X -> EnumSet.of(Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH);
            case Y -> EnumSet.of(Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH);
            case Z -> EnumSet.of(Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST);
        };
    }

    @Override
    public @Nullable Direction getRootFace(BlockState state) {
        return state.getValue(BlockStateProperties.FACING).getOpposite();
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, ctx.getClickedFace());
    }
}
