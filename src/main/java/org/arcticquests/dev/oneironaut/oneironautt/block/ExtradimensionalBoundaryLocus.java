package org.arcticquests.dev.oneironaut.oneironautt.block;

import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.utils.NBTHelper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.*;

public class ExtradimensionalBoundaryLocus extends BlockCircleComponent {

    public static final String TAG_BOUNDARY_LIST = "oneironaut:corners";

    public ExtradimensionalBoundaryLocus(Properties settings) {
        super(settings);
        this.registerDefaultState(this.getStateDefinition().any().setValue(BlockCircleComponent.ENERGIZED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockCircleComponent.ENERGIZED);
        builder.add(BlockStateProperties.FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(BlockStateProperties.FACING, pContext.getClickedFace());
    }

    @Override
    public ControlFlow acceptControlFlow(CastingImage imageIn, CircleCastEnv env, Direction enterDir, BlockPos pos, BlockState bs, ServerLevel world) {
        CompoundTag nbt = imageIn.component6();
        Set<BlockPos> visited = toPositionSet(nbt.getList(TAG_BOUNDARY_LIST, Tag.TAG_COMPOUND));
        visited.add(pos);
        ListTag serialized = toSerializedList(visited);
        //Oneironautfinal.LOGGER.info(serialized);
        NBTHelper.putList(nbt, TAG_BOUNDARY_LIST, serialized);

        List<Pair<BlockPos, Direction>> outputs = new ArrayList<>();
        for (Direction d : Direction.values()) {
            if (d != enterDir.getOpposite() && d != bs.getValue(BlockStateProperties.FACING)) {
                outputs.add(this.exitPositionFromDirection(pos, d));
            }
        }
        return new ControlFlow.Continue(imageIn, outputs);
    }

    @Override
    public boolean canEnterFromDirection(Direction enterDir, BlockPos pos, BlockState bs, ServerLevel world) {
        return enterDir != bs.getValue(BlockStateProperties.FACING).getOpposite();
    }

    @Override
    public EnumSet<Direction> possibleExitDirections(BlockPos pos, BlockState bs, Level world) {
        EnumSet<Direction> enumset = EnumSet.allOf(Direction.class);
        enumset.remove(bs.getValue(BlockStateProperties.FACING));
        return enumset;
    }

    @Override
    public BlockState startEnergized(BlockPos pos, BlockState bs, Level world) {
        BlockState newState = bs.setValue(BlockCircleComponent.ENERGIZED, true);
        world.setBlockAndUpdate(pos, newState);
        return newState;
    }

    @Override
    public boolean isEnergized(BlockPos pos, BlockState bs, Level world) {
        return bs.getValue(BlockCircleComponent.ENERGIZED);
    }

    @Override
    public BlockState endEnergized(BlockPos pos, BlockState bs, Level world) {
        BlockState newState = bs.setValue(BlockCircleComponent.ENERGIZED, false);
        world.setBlockAndUpdate(pos, newState);
        return newState;
    }

    @Override
    public Direction normalDir(BlockPos pos, BlockState bs, Level world, int recursionLeft) {
        return bs.getValue(BlockStateProperties.FACING);
    }

    @Override
    public float particleHeight(BlockPos pos, BlockState bs, Level world) {
        return 1f / 8f;
    }

    public static Set<BlockPos> toPositionSet(ListTag list) {
        Set<BlockPos> output = new HashSet<>();
        for (Tag element : list){
            output.add(NbtUtils.readBlockPos((CompoundTag) element));
        }
        return output;
    }

    public static ListTag toSerializedList(Set<BlockPos> set){
        ListTag output = new ListTag();
        for (BlockPos pos : set){
            output.add(NbtUtils.writeBlockPos(pos));
        }
        return output;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context){
        return switch (state.getValue(BlockStateProperties.FACING)){
            case DOWN -> Shapes.box(0.0 / 16, 8.0 / 16, 0.0 / 16, 16.0 / 16, 16.0 / 16, 16.0 / 16);
            case UP -> Shapes.box(0.0 / 16, 0.0 / 16, 0.0 / 16, 16.0 / 16, 8.0 / 16, 16.0 / 16);
            case NORTH -> Shapes.box(0.0 / 16, 0.0 / 16, 8.0 / 16, 16.0 / 16, 16.0 / 16, 16.0 / 16);
            case SOUTH -> Shapes.box(0.0 / 16, 0.0 / 16, 0.0 / 16, 16.0 / 16, 16.0 / 16, 8.0 / 16);
            case WEST -> Shapes.box(8.0 / 16, 0.0 / 16, 0.0 / 16, 16.0 / 16, 16.0 / 16, 16.0 / 16);
            case EAST -> Shapes.box(0.0 / 16, 0.0 / 16, 0.0 / 16, 8.0 / 16, 16.0 / 16, 16.0 / 16);
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context){
        return switch (state.getValue(BlockStateProperties.FACING)){
            case DOWN -> Shapes.box(0.0 / 16, 8.0 / 16, 0.0 / 16, 16.0 / 16, 16.0 / 16, 16.0 / 16);
            case UP -> Shapes.box(0.0 / 16, 0.0 / 16, 0.0 / 16, 16.0 / 16, 8.0 / 16, 16.0 / 16);
            case NORTH -> Shapes.box(0.0 / 16, 0.0 / 16, 8.0 / 16, 16.0 / 16, 16.0 / 16, 16.0 / 16);
            case SOUTH -> Shapes.box(0.0 / 16, 0.0 / 16, 0.0 / 16, 16.0 / 16, 16.0 / 16, 8.0 / 16);
            case WEST -> Shapes.box(8.0 / 16, 0.0 / 16, 0.0 / 16, 16.0 / 16, 16.0 / 16, 16.0 / 16);
            case EAST -> Shapes.box(0.0 / 16, 0.0 / 16, 0.0 / 16, 8.0 / 16, 16.0 / 16, 16.0 / 16);
        };
    }
}