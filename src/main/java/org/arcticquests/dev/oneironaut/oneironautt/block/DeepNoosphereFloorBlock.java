package org.arcticquests.dev.oneironaut.oneironautt.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DeepNoosphereFloorBlock extends Block {
    public DeepNoosphereFloorBlock(Properties settings) {
        super(settings);
    }

    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context){
        return Shapes.block();
    }

    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter world, BlockPos pos) {
        return Shapes.block();
    }

    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.block();
    }

    /*public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        double y = pos.getY() < 0.0 ? 256.0 : 0.0;
        entity.teleport(pos.getX(), y, pos.getZ());
    }

    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        double y = pos.getY() < 0.0 ? 256.0 : 0.0;
        entity.teleport(pos.getX(), y, pos.getZ());
    }

    public void onEntityLand(BlockView world, Entity entity) {
        Vec3d pos = entity.getPos();
        double y = pos.getY() < 0.0 ? 256.0 : 0.0;
        entity.teleport(pos.getX(), y, pos.getZ());
    }

    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        double y = pos.getY() < 0.0 ? 256.0 : 0.0;
        entity.teleport(pos.getX(), y, pos.getZ());
    }*/
}
