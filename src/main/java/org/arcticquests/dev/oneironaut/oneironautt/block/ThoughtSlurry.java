package org.arcticquests.dev.oneironaut.oneironautt.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.fluids.FluidType;
import org.arcticquests.dev.oneironaut.oneironautt.Oneironaut;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautFluidTypes;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautItemRegistry;
import org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautMiscRegistry;

public class ThoughtSlurry extends FlowingFluid {
    public static final ResourceLocation ID = ResourceLocation.tryBuild(Oneironaut.MODID, "thought_slurry");
    public static final ResourceLocation FLOWING_ID = ResourceLocation.tryBuild(Oneironaut.MODID, "flowing_thought_slurry");
    public static final TagKey<Fluid> TAG = TagKey.create(net.minecraft.core.registries.Registries.FLUID, ThoughtSlurry.ID);
    public static final BlockBehaviour.Properties SETTINGS =
            BlockBehaviour.Properties.copy(Blocks.WATER).noOcclusion().mapColor(MapColor.COLOR_PURPLE);

    @Override
    public boolean isSame(Fluid fluid) {
        return fluid == getSource() || fluid == getFlowing();
    }

    @Override
    public Fluid getFlowing() {
        return OneironautMiscRegistry.THOUGHT_SLURRY_FLOWING.get();
    }

    @Override
    public Fluid getSource() {
        return OneironautMiscRegistry.THOUGHT_SLURRY.get();
    }

    @Override
    public FluidState getFlowing(int level, boolean falling) {
        return this.getFlowing().defaultFluidState().setValue(LEVEL, level).setValue(FALLING, falling);
    }

    @Override
    protected boolean canConvertToSource(Level world) {
        return true;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor world, BlockPos pos, BlockState state) {
        final var blockEntity = state.hasBlockEntity() ? world.getBlockEntity(pos) : null;
        Block.dropResources(state, world, pos, blockEntity);
    }


    @Override
    protected int getSlopeFindDistance(LevelReader world) {
        return 3;
    }

    @Override
    protected int getDropOff(LevelReader world) {
        return 1;
    }

    @Override
    public Item getBucket() {
        return OneironautItemRegistry.THOUGHT_SLURRY_BUCKET.get();
    }

    @Override
    protected boolean canBeReplacedWith(FluidState state, BlockGetter world, BlockPos pos, Fluid fluid, Direction direction) {
        return false;
    }

    @Override
    public int getTickDelay(LevelReader world) {
        return 5;
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0f;
    }

    @Override
    protected BlockState createLegacyBlock(FluidState state) {
        // Don't use any static INSTANCE - always get via registry!
        return org.arcticquests.dev.oneironaut.oneironautt.registry.OneironautBlockRegistry.THOUGHT_SLURRY_BLOCK.get()
                .defaultBlockState()
                .setValue(net.minecraft.world.level.block.LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    @Override
    public boolean isSource(FluidState state) {
        return state.isSource();
    }

    @Override
    public int getAmount(FluidState state) {
        return 8;
    }

    @Override
    public FluidType getFluidType() {
        return OneironautFluidTypes.THOUGHT_SLURRY_TYPE.get();
    }

    public static class Flowing extends ThoughtSlurry {
        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(FlowingFluid.LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(FlowingFluid.LEVEL);
        }
    }

    public static class Still extends ThoughtSlurry {
        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(FlowingFluid.LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }

        @Override
        public int getAmount(FluidState state) {
            return 8;
        }
    }
}