package org.arcticquests.dev.oneironaut.oneironautt.block.blockentity;

import at.petrak.hexcasting.common.misc.AkashicTreeGrower;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry;

public class EdifiedTreeSpawnerBlockEntity extends BlockEntity {
    public EdifiedTreeSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(OneironautBlockRegistry.EDIFIED_TREE_SPAWNER_ENTITY.get(), pos, state);
    }
    public void tick(Level world, BlockPos pos, BlockState state){
        if (!world.isClientSide && world instanceof ServerLevel serverWorld){
            //world.setBlockState(pos, Blocks.OAK_SAPLING.getDefaultState());
            AkashicTreeGrower.INSTANCE.growTree(serverWorld, serverWorld.getChunkSource().getGenerator(), pos, state, world.random);
        }
    }
}
