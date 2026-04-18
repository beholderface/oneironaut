package org.arcticquests.dev.oneironaut.oneironautt.block;

import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AmethystClusterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.block.blockentity.HoverElevatorBlockEntity;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry;

public class SuperBuddingBlock extends Block /*extends BuddingAmethystBlock*/ {
    public SuperBuddingBlock(Properties settings){
        super(settings
                .randomTicks()
                .sound(SoundType.AMETHYST)
                .destroyTime(3.5f));
    }

    public static final int GROW_CHANCE = 3;
    private static final Direction[] UPDATE_SHAPE_ORDER = Direction.values();

    //shamelessly stolen from vanilla code
    @Override
    public void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if (random.nextInt(GROW_CHANCE) == 0) {
            Direction direction = UPDATE_SHAPE_ORDER[random.nextInt(UPDATE_SHAPE_ORDER.length)];
            BlockPos blockPos = pos.relative(direction);
            BlockState blockState = world.getBlockState(blockPos);
            Block block = null;
            if (Oneironaut.isWorldNoosphere(world)){
                if (canGrowIn(blockState)) {
                    block = OneironautBlockRegistry.PSEUDOAMETHYST_BUD_SMALL.get();
                } else if (blockState.is(OneironautBlockRegistry.PSEUDOAMETHYST_BUD_SMALL.get()) && blockState.getValue(AmethystClusterBlock.FACING) == direction) {
                    block = OneironautBlockRegistry.PSEUDOAMETHYST_BUD_MEDIUM.get();
                } else if (blockState.is(OneironautBlockRegistry.PSEUDOAMETHYST_BUD_MEDIUM.get()) && blockState.getValue(AmethystClusterBlock.FACING) == direction) {
                    block = OneironautBlockRegistry.PSEUDOAMETHYST_BUD_LARGE.get();
                } else if (blockState.is(OneironautBlockRegistry.PSEUDOAMETHYST_BUD_LARGE.get()) && blockState.getValue(AmethystClusterBlock.FACING) == direction) {
                    block = OneironautBlockRegistry.PSEUDOAMETHYST_CLUSTER.get();
                }

                if (block != null) {
                    BlockState blockState2 = (BlockState)((BlockState)block.defaultBlockState().setValue(AmethystClusterBlock.FACING, direction)).setValue(AmethystClusterBlock.WATERLOGGED, blockState.getFluidState().getType() == Fluids.WATER);
                    world.setBlockAndUpdate(blockPos, blockState2);
                }

            } else {
                if (canGrowIn(blockState)) {
                    block = Blocks.MEDIUM_AMETHYST_BUD;
                } else if (blockState.is(Blocks.MEDIUM_AMETHYST_BUD) && blockState.getValue(AmethystClusterBlock.FACING) == direction) {
                    block = Blocks.LARGE_AMETHYST_BUD;
                } else if (blockState.is(Blocks.LARGE_AMETHYST_BUD) && blockState.getValue(AmethystClusterBlock.FACING) == direction) {
                    block = Blocks.AMETHYST_CLUSTER;
                }

                if (block != null) {
                    BlockState blockState2 = (BlockState)((BlockState)block.defaultBlockState().setValue(AmethystClusterBlock.FACING, direction)).setValue(AmethystClusterBlock.WATERLOGGED, blockState.getFluidState().getType() == Fluids.WATER);
                    world.setBlockAndUpdate(blockPos, blockState2);
                }
            }
        }
    }

    public static boolean canGrowIn(BlockState state) {
        return state.isAir() || state.is(Blocks.WATER) && state.getFluidState().getAmount() == 8;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        RandomSource rand = world.random;
        Vec3 particleCenter = Vec3.atCenterOf(new Vec3i(pos.getX(), pos.getY(), pos.getZ()));
        int limit = rand.nextIntBetweenInclusive(3, 6);
        for (int i = 0; i < limit; i++){
            world.addParticle(new ConjureParticleOptions(HoverElevatorBlockEntity.color),
                    particleCenter.x + (((rand.nextGaussian() * 2) - 1) / 7), particleCenter.y + (((rand.nextGaussian() * 2) - 1) / 7),
                    particleCenter.z + (((rand.nextGaussian() * 2) - 1) / 7), 0.0, 0.0, 0.0);
        }
    }
}
