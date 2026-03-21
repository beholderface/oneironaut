package org.arcticquests.dev.oneironaut.oneironautt.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.AbstractGlassBlock;
import net.minecraft.world.level.block.state.BlockState;

public class RaycastBlockerGlass extends AbstractGlassBlock {
    public RaycastBlockerGlass(Properties settings) {
        super(settings);
    }

    public boolean isTranslucent(BlockState state, BlockGetter world, BlockPos pos) {
        return false;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return world.getMaxLightLevel();
    }
}
