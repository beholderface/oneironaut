package org.arcticquests.dev.oneironaut.oneironautt.block;

import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.HoverElevatorBlockEntity;

public class HoverRepeaterBlock extends Block {
    public HoverRepeaterBlock(Properties settings) {
        super(settings);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        RandomSource rand = world.random;
        Vec3 particleCenter = Vec3.atCenterOf(new Vec3i(pos.getX(), pos.getY(), pos.getZ())).add(0.0, 0.2, 0.0);
        int limit = rand.nextIntBetweenInclusive(1, 3);
        for (int i = 0; i < limit; i++){
            world.addParticle(new ConjureParticleOptions(HoverElevatorBlockEntity.color),
                    particleCenter.x + (((rand.nextGaussian() * 2) - 1) / 50), particleCenter.y + (((rand.nextGaussian() * 2) - 1) / 50),
                    particleCenter.z + (((rand.nextGaussian() * 2) - 1) / 50), 0.0, 0.0, 0.0);
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext context){
        VoxelShape center = Shapes.box(7f / 16, 2f / 16, 7f / 16, 9f / 16, 12f / 16, 9f / 16);
        VoxelShape base1 = Shapes.box(5f / 16, 3f / 16, 5f / 16, 11f / 16, 4f / 16, 11f / 16);
        VoxelShape base2 = Shapes.box(6f / 16, 2f / 16, 6f / 16, 10f / 16, 3f / 16, 10f / 16);
        return Shapes.or(center, base1, base2);
    }

    @Override
    public RenderShape getRenderShape(BlockState state){
        return RenderShape.MODEL;
    }
}
