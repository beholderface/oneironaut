package org.arcticquests.dev.oneironaut.oneironautt.block.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry;

public class InstantBreakingBlockEntity extends BlockEntity {
    public InstantBreakingBlockEntity(BlockPos pos, BlockState state) {
        super(OneironautBlockRegistry.INSTANT_BREAKER_ENTITY.get(), pos, state);
    }

    public static <T extends BlockEntity> void tick(Level world, BlockPos pos){
        world.destroyBlock(pos, true);
    }
}
