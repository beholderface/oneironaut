package org.arcticquests.dev.oneironaut.oneironautt.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautItemRegistry;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautMiscRegistry;

public interface Slurryloggable extends SimpleWaterloggedBlock {
    BooleanProperty slurrylogged = BooleanProperty.create("slurrylogged");

    @Override
    default boolean canPlaceLiquid(BlockGetter world, BlockPos pos, BlockState state, Fluid fluid){
        // Compare against registered fluid instance!
        return fluid == Fluids.WATER || fluid == OneironautMiscRegistry.THOUGHT_SLURRY.get();
    }

    @Override
    default boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!(state.getValue(BlockStateProperties.WATERLOGGED) || state.getValue(slurrylogged))) {
            BooleanProperty property = null;
            if (fluidState.getType() == Fluids.WATER){
                property = BlockStateProperties.WATERLOGGED;
            } else if (fluidState.getType() == OneironautMiscRegistry.THOUGHT_SLURRY.get()) {
                property = slurrylogged;
            }
            if (!world.isClientSide() && property != null) {
                world.setBlock(pos, state.setValue(property, true), 3);
                world.scheduleTick(pos, fluidState.getType(), fluidState.getType().getTickDelay(world));
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    default ItemStack pickupBlock(LevelAccessor world, BlockPos pos, BlockState state) {
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            world.setBlock(pos, state.setValue(BlockStateProperties.WATERLOGGED, false), 3);
            if (!state.canSurvive(world, pos)) {
                world.destroyBlock(pos, true);
            }
            return new ItemStack(Items.WATER_BUCKET);
        } else if (state.getValue(slurrylogged)){
            world.setBlock(pos, state.setValue(slurrylogged, false), 3);
            if (!state.canSurvive(world, pos)) {
                world.destroyBlock(pos, true);
            }
            return new ItemStack(OneironautItemRegistry.THOUGHT_SLURRY_BUCKET.get());
        } else {
            return ItemStack.EMPTY;
        }
    }
}